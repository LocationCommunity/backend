package com.easytrip.backend.member.service.impl;


import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.components.MailComponents;
import com.easytrip.backend.exception.UnsupportedImageTypeException;
import com.easytrip.backend.exception.impl.*;
import com.easytrip.backend.matching.domain.MemberInterestEntity;
import com.easytrip.backend.matching.repository.InterestRepository;
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
import com.easytrip.backend.type.*;
import io.jsonwebtoken.Claims;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
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
  private final InterestRepository interestRepository;
  private final RestTemplate restTemplate = new RestTemplate();

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
//      String projectPath =
//          System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String projectPath = System.getProperty("user.home") + "\\Desktop\\images\\";
      String fileName = uuid + "_" + file.getOriginalFilename();

      // 파일 이름에서 확장자 추출
      String fileExtension = StringUtils.getFilenameExtension(fileName);

      // 지원하는 이미지 파일 확장자 목록
      List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

      // 확장자가 이미지 파일인지 확인
      if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {
        File saveFile = new File(projectPath, fileName);
        try {
          file.transferTo(saveFile);
        } catch (Exception e) {
          throw new ImageSaveException();
        }
      } else {
        // 이미지 파일이 아닌 경우에 대한 처리
        throw new UnsupportedImageTypeException();
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
    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    // Redis에 해당 유저의 email로 저장된 refreshToken이 있는지 확인 후 있으면 삭제
    if (redisTemplate.opsForValue()
        .get("RefreshToken: " + authentication.getName() + ", Platform: " + platform) != null) {
      redisTemplate.delete("RefreshToken: " + authentication.getName() + ", Platform: " + platform);
    }

    // 해당 accessToken 유효시간을 가지고 와서 Redis에 BlackList로 추가
    long expiration = jwtTokenProvider.getExpiration(accessToken);
    long now = (new Date()).getTime();
    long accessTokenExpiresIn = expiration - now;
    redisTemplate.opsForValue()
        .set(accessToken, "logout", accessTokenExpiresIn, TimeUnit.MILLISECONDS);

    // 카카오 로그인 일 때
    if (platform == Platform.KAKAO) {
      String email = authentication.getName();

      MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
          .orElseThrow(() -> new NotFoundMemberException());
      String snsToken = member.getSnsToken();

      // kakao logout API 호출
      String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + snsToken);
      HttpEntity<String> entity = new HttpEntity<>(headers);

      restTemplate.exchange(kakaoLogoutUrl, HttpMethod.POST, entity, String.class);
    }
  }

  @Override
  public void withdrawal(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();
    Platform platform = jwtTokenProvider.getPlatform(accessToken);

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
    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    return MemberDto.of(member);
  }
  @Transactional
  @Override
  public MemberDto update(String accessToken, UpdateRequest updateRequest, MultipartFile file) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();
    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    String imageUrl;

    // 프로필 이미지 저장
    if (file.isEmpty() || file == null) {
      imageUrl = null;
    } else {
      String uuid = UUID.randomUUID().toString();

//      String projectPath =
//          System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String projectPath = System.getProperty("user.home") + "\\Desktop\\images\\";
      String fileName = uuid + "_" + file.getOriginalFilename();


      // 파일 이름에서 확장자 추출
      String fileExtension = StringUtils.getFilenameExtension(fileName);

      // 지원하는 이미지 파일 확장자 목록
      List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

      // 확장자가 이미지 파일인지 확인
      if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {
        File saveFile = new File(projectPath, fileName);
        try {
          file.transferTo(saveFile);
        } catch (Exception e) {
          throw new ImageSaveException();
        }
      } else {
        // 이미지 파일이 아닌 경우에 대한 처리
        throw new UnsupportedImageTypeException();
      }

      ImageEntity image = ImageEntity.builder()
          .fileName(fileName)
          .filePath(projectPath + "\\" + fileName)
          .useType(UseType.PROFILE)
          .memberId(member)
          .build();
      imageRepository.save(image);

      imageUrl = image.getFileName();
    }

    MemberEntity updateMember = member.toBuilder()
        .nickname(updateRequest.getNickname())
        .imageUrl(imageUrl)
        .introduction(updateRequest.getIntroduction())
        .build();
    memberRepository.save(updateMember);

    List<BoardEntity> boards = boardRepository.findByMemberId(member);
    for (BoardEntity board : boards) {
      board.setNickname(updateRequest.getNickname());
    }
    boardRepository.saveAll(boards);

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
      String projectPath =
          System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String fileName = uuid + "_" + file.getOriginalFilename();

      // 파일 이름에서 확장자 추출
      String fileExtension = StringUtils.getFilenameExtension(fileName);

      // 지원하는 이미지 파일 확장자 목록
      List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

      // 확장자가 이미지 파일인지 확인
      if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {
        File saveFile = new File(projectPath, fileName);
        try {
          file.transferTo(saveFile);
        } catch (Exception e) {
          throw new ImageSaveException();
        }
      } else {
        // 이미지 파일이 아닌 경우에 대한 처리
        throw new UnsupportedImageTypeException();
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
          .orElseThrow(NotFoundMemberException::new);

      List<MemberDetailDto> list = new ArrayList<>();
      list.add(MemberDetailDto.of(member));
      return list;
    }

    throw new InvalidSearchOptionException();
  }

  @Override
  public void setInterest(String accessToken, List<Interest> interestList) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    // 이미 관심사 설정이 완료된 회원일 경우
    List<MemberInterestEntity> memberInterestList = interestRepository.findAllByMemberId(member);
    if (memberInterestList.size() == 3) {
      throw new InterestValidationException();
    }

    for (Interest interest : interestList) {
      MemberInterestEntity memberInterest = MemberInterestEntity.builder()
          .memberId(member)
          .interest(interest)
          .build();
      interestRepository.save(memberInterest);
    }
  }

  @Override
  public void changeInterest(String accessToken, List<Interest> interestList) {
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    interestRepository.deleteAllByMemberId(member);

    for (Interest interest : interestList) {
      MemberInterestEntity memberInterest = MemberInterestEntity.builder()
          .memberId(member)
          .interest(interest)
          .build();
      interestRepository.save(memberInterest);
    }
  }

  private TokenCreateDto snsLogin(MemberEntity snsMember, Platform platForm) {
    Optional<MemberEntity> byEmail = memberRepository.findByEmailAndPlatform(snsMember.getEmail(), platForm);

    // 기존에 가입한 회원
    if (byEmail.isPresent()) {
      MemberEntity member = byEmail.get();

      if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
        throw new SuspendedMemberException();
      } else if (member.getStatus().equals(MemberStatus.WITHDRAWN)) {
        member = member.toBuilder()
            .imageUrl(snsMember.getImageUrl())
            .status(MemberStatus.ACTIVE)
            .regDate(LocalDateTime.now())
            .build();
      }

      member = member.toBuilder()
          .snsToken(snsMember.getSnsToken())
          .build();
      memberRepository.save(member);

      return TokenCreateDto.builder()
          .email(member.getEmail())
          .adminYn(member.getAdminYn())
          .build();
    } else {
      // 새로운 회원(sns로 회원가입과 동시에 로그인)
      MemberEntity newMember = snsMember.toBuilder()
          .auth(true)
          .adminYn(false)
          .status(MemberStatus.ACTIVE)
          .regDate(LocalDateTime.now())
          .snsToken(snsMember.getSnsToken())
          .build();
      memberRepository.save(newMember);

      return TokenCreateDto.builder()
          .email(newMember.getEmail())
          .adminYn(newMember.getAdminYn())
          .build();
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