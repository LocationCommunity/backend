package com.easytrip.backend.member.service.impl;


import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.common.image.domain.ImageEntity;

import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.components.MailComponents;
import com.easytrip.backend.exception.impl.AlreadyAuthenticatedException;
import com.easytrip.backend.exception.impl.DuplicateEmailException;
import com.easytrip.backend.exception.impl.ExpiredException;
import com.easytrip.backend.exception.impl.ImageSaveException;
import com.easytrip.backend.exception.impl.InvalidAuthCodeException;
import com.easytrip.backend.exception.impl.InvalidEmailException;
import com.easytrip.backend.exception.impl.InvalidPasswordConfirmationException;
import com.easytrip.backend.exception.impl.InvalidPasswordException;
import com.easytrip.backend.exception.impl.InvalidSearchOptionException;
import com.easytrip.backend.exception.impl.InvalidStatusException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.exception.impl.SuspendedMemberException;
import com.easytrip.backend.exception.impl.WaitingMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.MemberDto;
import com.easytrip.backend.member.dto.TokenCreateDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.member.service.ManagementService;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.Platform;
import com.easytrip.backend.type.UseType;
import io.jsonwebtoken.Claims;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ManagementServiceImpl implements ManagementService {

  private final MemberRepository memberRepository;
  private final MailComponents mailComponents;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate redisTemplate;
  private final PasswordEncoder passwordEncoder;
  private final ImageRepository imageRepository;

  private final BoardRepository boardRepository;



  @Override
  public void signUp(SignUpRequest signUpRequest, MultipartFile file, Platform platForm) {

    // 올바르지 않은 이메일
    if (!isValidEmail(signUpRequest.getEmail())) {
      throw new InvalidEmailException();
    }

    // 비밀번호 체크가 불일치
    if (!signUpRequest.getPassword().equals(signUpRequest.getCheckPassword())) {
      throw new InvalidPasswordConfirmationException();
    }

    MemberEntity member = null;

    // 중복가입인지 확인
    Optional<MemberEntity> byEmail = memberRepository.findByEmailAndPlatform(
        signUpRequest.getEmail(), platForm);
    if (byEmail.isPresent()) {
      member = byEmail.get();

      // 탈퇴상태가 아니면 중복가입 exception
      if (!member.getStatus().equals(MemberStatus.WITHDRAWN)) {
        throw new DuplicateEmailException();
      }
    }

    if (member == null) {
      member = new MemberEntity();
    }


    // 프로필 이미지 저장
    if (file.isEmpty() || file == null) {
      member = memberRepository.save(SignUpRequest.signUpInput(member, signUpRequest, null));
    } else {
      String uuid = UUID.randomUUID().toString();
      String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String fileName = uuid + "_" + file.getOriginalFilename();
      File saveFile = new File(projectPath, fileName);
      try {
        file.transferTo(saveFile);
      } catch (Exception e) {

        throw new ImageSaveException();

      }

      ImageEntity image = ImageEntity.builder()
          .fileName(fileName)
          .filePath(projectPath + "\\" + fileName)
          .useType(UseType.PROFILE)
          .memberId(member)
          .build();

      member = memberRepository.save(SignUpRequest.signUpInput(member, signUpRequest, image));

      ImageEntity imageEntity = image.toBuilder()
          .memberId(member)
          .build();
      imageRepository.save(imageEntity);
    }



    sendMail(signUpRequest, member);
  }

  @Override
  public void auth(String email, String code, Platform platform) {

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    if (member.getAuth()) {
      throw new AlreadyAuthenticatedException();
    }

    if (!code.equals(member.getAuthCode())) {
      throw new InvalidAuthCodeException();
    }

    MemberEntity memberEntity = member.toBuilder()
        .auth(true)
        .status(MemberStatus.ACTIVE)
        .build();
    memberRepository.save(memberEntity);
  }

  @Override
  public TokenCreateDto login(LoginRequest loginRequest, Platform platForm) {

    MemberEntity member = memberRepository.findByEmailAndPlatform(loginRequest.getEmail(), platForm)
        .orElseThrow(() -> new NotFoundMemberException());

    // 회원 상태에 따른 exception
    if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
      throw new SuspendedMemberException();
    } else if (member.getStatus().equals(MemberStatus.WAITING_FOR_APPROVAL)) {
      throw new WaitingMemberException();
    } else if (member.getStatus().equals(MemberStatus.WITHDRAWN)) {
      throw new NotFoundMemberException();
    }

    // 비밀번호 확인
    if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      throw new InvalidPasswordException();
    }

    TokenCreateDto result = TokenCreateDto.builder()
        .email(member.getEmail())
        .adminYn(member.getAdminYn())
        .build();

    return result;
  }

  @Override
  public TokenCreateDto naverLogin(MemberEntity naverMember, Platform platForm) {

    return snsLogin(naverMember, platForm);
  }

  @Override
  public TokenCreateDto kakaoLogin(MemberEntity kakaoMember, Platform platForm) {

    return snsLogin(kakaoMember, platForm);
  }

  @Override
  public void logout(String accessToken) {

    // 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // 토큰을 통해 사용자 정보 받아오기
    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    // Redis에 해당 유저의 email로 저장된 refreshToken이 있는지 확인 후 있으면 삭제
    if (redisTemplate.opsForValue().get("RefreshToken: " + authentication.getName() + ", Platform: " + platform) != null) {
      redisTemplate.delete("RefreshToken: " + authentication.getName() + ", Platform: " + platform);
    }

    // 해당 accessToken 유효시간을 가지고 와서 Redis에 BlackList로 추가
    long expiration = jwtTokenProvider.getExpiration(accessToken);
    long now = (new Date()).getTime();
    long accessTokenExpiresIn = expiration - now;
    redisTemplate.opsForValue()
        .set(accessToken, "logout", accessTokenExpiresIn, TimeUnit.MILLISECONDS);
  }

  @Override
  public void withdrawal(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    // 탈퇴한 회원의 상태를 탈퇴로 변경
    MemberEntity withdrawnMember = member.toBuilder()
        .status(MemberStatus.WITHDRAWN)
        .build();
    memberRepository.save(withdrawnMember);

    // 나중에 탈퇴한 회원이 작성한 게시물을 어떻게 할지 작성
  }

  @Override
  public void resetPassword(ResetRequest resetRequest, Platform platform) {

    MemberEntity member = memberRepository.findByEmailAndPlatform(resetRequest.getEmail(), platform)
        .orElseThrow(() -> new NotFoundMemberException());

    String uuid = UUID.randomUUID().toString();
    String encPassword = BCrypt.hashpw(resetRequest.getResetPassword(), BCrypt.gensalt());

    // 비밀번호 변경코드와 기간 발급
    MemberEntity memberEntity = member.toBuilder()
        .passwordAuthCode(uuid)
        .passwordDate(LocalDateTime.now().plusMinutes(30))
        .build();
    memberRepository.save(memberEntity);

    String email = member.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "EzTrip 비밀번호 변경";
    String message = "<h3>EzTrip 비밀번호 변경을 위해서 아래의 링크를 클릭하셔서 인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/members/password?email=" + email + "&code="
        + memberEntity.getPasswordAuthCode() + "&resetPassword=" + encPassword
        + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);
  }

  @Override
  public void passwordAuth(String email, String code, String resetPassword, Platform platform) {

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    // 인증 코드가 다르면 exception 발생
    if (!code.equals(member.getPasswordAuthCode())) {
      throw new InvalidAuthCodeException();
    }

    // 비밀번호 변경 기간이 지나면 exception 발생
    if (member.getPasswordDate().isBefore(LocalDateTime.now())) {
      throw new ExpiredException();
    }

    MemberEntity memberEntity = member.toBuilder()
        .password(resetPassword)
        .passwordAuthCode(null)
        .passwordDate(null)
        .build();
    memberRepository.save(memberEntity);
  }

  @Override
  public MemberDto myInfo(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    return MemberDto.of(member);
  }

  @Override
  public MemberDto update(String accessToken, UpdateRequest updateRequest, MultipartFile file) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());


    String imageUrl;

    // 프로필 이미지 저장
    if (file.isEmpty() || file == null) {
      imageUrl = null;
    } else {
      String uuid = UUID.randomUUID().toString();
      String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String fileName = uuid + "_" + file.getOriginalFilename();
      File saveFile = new File(projectPath, fileName);
      try {
        file.transferTo(saveFile);
      } catch (Exception e) {

        throw new ImageSaveException();

      }

      ImageEntity image = ImageEntity.builder()
          .fileName(fileName)
          .filePath(projectPath + "\\" + fileName)
          .useType(UseType.PROFILE)
          .memberId(member)
          .build();
      imageRepository.save(image);

      imageUrl = image.getFilePath();
    }

    MemberEntity updateMember = member.toBuilder()
        .nickname(updateRequest.getNickname())
        .imageUrl(imageUrl)
        .introduction(updateRequest.getIntroduction())
        .build();
    memberRepository.save(updateMember);

    return MemberDto.of(updateMember);
  }

  @Override
  public void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    // 이미 설정하려는 상태일 경우, 잘못된 설정일 때
    if (member.getStatus().equals(memberStatus) || memberStatus == null
        || memberStatus.equals(MemberStatus.WITHDRAWN) || memberStatus.equals(
        MemberStatus.WAITING_FOR_APPROVAL)) {
      throw new InvalidStatusException();
    }

    if (memberStatus.equals(MemberStatus.SUSPENDED)) {
      MemberEntity memberEntity = member.toBuilder()
          .status(memberStatus)
          .build();
      memberRepository.save(memberEntity);

      // 정지된 회원이 작성한 게시글 삭제
      List<BoardEntity> posts = boardRepository.findByMemberId(member);
      for (BoardEntity post : posts) {
        BoardEntity board = post.toBuilder()
            .status(BoardStatus.INACTIVE)
            .build();
        boardRepository.save(board);
      }
    } else if (memberStatus.equals(MemberStatus.ACTIVE)) {
      MemberEntity memberEntity = member.toBuilder()
          .status(memberStatus)
          .build();
      memberRepository.save(memberEntity);
    }
  }

  @Override
  public MemberDetailDto getMemberInfo(String accessToken, Long memberId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    return MemberDetailDto.of(member);
  }

  @Override
  public MemberDetailDto updateMemberInfo(String accessToken, Long memberId,
      UpdateRequest updateRequest, MultipartFile file) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    MemberEntity updateMember = new MemberEntity();

    if (file.isEmpty() || file == null) {
      updateMember = member.toBuilder()
          .nickname(updateRequest.getNickname())
          .introduction(updateRequest.getIntroduction())
          .build();
    } else {
      String uuid = UUID.randomUUID().toString();
      String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String fileName = uuid + "_" + file.getOriginalFilename();
      File saveFile = new File(projectPath, fileName);
      try {
        file.transferTo(saveFile);
      } catch (Exception e) {
        throw new ImageSaveException();
      }

      ImageEntity image = ImageEntity.builder()
          .fileName(fileName)
          .filePath(projectPath + "\\" + fileName)
          .useType(UseType.PROFILE)
          .memberId(member)
          .build();
      imageRepository.save(image);

      updateMember = member.toBuilder()
          .nickname(updateRequest.getNickname())
          .imageUrl(image.getFilePath())
          .introduction(updateRequest.getIntroduction())
          .build();
    }

    memberRepository.save(updateMember);

    return MemberDetailDto.of(updateMember);
  }

  @Override
  public List<MemberDetailDto> searchMember(String accessToken, String keyword,
      SearchOption searchOption) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    if (searchOption.equals(SearchOption.NAME)) {
      List<MemberEntity> byName = memberRepository.findByName(keyword);
      if (byName.isEmpty()) {
        throw new NotFoundMemberException();
      }

      return MemberDetailDto.listOf(byName);
    } else if (searchOption.equals(SearchOption.NICKNAME)) {
      MemberEntity member = memberRepository.findByNickname(keyword)
          .orElseThrow(() -> new NotFoundMemberException());

      List<MemberDetailDto> list = new ArrayList<>();
      list.add(MemberDetailDto.of(member));
      return list;
    }

    throw new InvalidSearchOptionException();
  }

  private TokenCreateDto snsLogin(MemberEntity snsMember, Platform platForm) {
    Optional<MemberEntity> byEmail = memberRepository.findByEmailAndPlatform(snsMember.getEmail(),
        platForm);
    // 기존에 가입한 회원
    if (byEmail.isPresent()) {
      MemberEntity member = byEmail.get();

      if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
        throw new SuspendedMemberException();
      } else if (member.getStatus().equals(MemberStatus.WITHDRAWN)) {
        MemberEntity memberEntity = member.toBuilder()
            .status(MemberStatus.ACTIVE)
            .regDate(LocalDateTime.now())
            .build();
        memberRepository.save(memberEntity);
      }

      TokenCreateDto result = TokenCreateDto.builder()
          .email(member.getEmail())
          .adminYn(member.getAdminYn())
          .build();
      return result;
    } else {
      // 새로운 회원(sns로 회원가입과 동시에 로그인)
      MemberEntity newMember = snsMember.toBuilder()
          .auth(true)
          .adminYn(false)
          .status(MemberStatus.ACTIVE)
          .regDate(LocalDateTime.now())
          .build();
      memberRepository.save(newMember);

      TokenCreateDto result = TokenCreateDto.builder()
          .email(newMember.getEmail())
          .adminYn(newMember.getAdminYn())
          .build();
      return result;
    }
  }

  private void sendMail(SignUpRequest signUpRequest, MemberEntity member) {
    String email = signUpRequest.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "EzTrip 회원인증 메일";
    String message = "<h3>EzTrip 회원가입에 성공했습니다. 아래의 링크를 클릭하셔서 회원인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/members/auth?email=" + email + "&code="
        + member.getAuthCode() + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);
  }

  private boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
