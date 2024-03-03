package com.easytrip.backend.member.service;

import com.easytrip.backend.components.MailComponents;
import com.easytrip.backend.exception.impl.DuplicateEmailException;
import com.easytrip.backend.exception.impl.InvalidAuthCodeException;
import com.easytrip.backend.exception.impl.InvalidEmailException;
import com.easytrip.backend.exception.impl.InvalidPasswordConfirmationException;
import com.easytrip.backend.exception.impl.InvalidPasswordException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.exception.impl.PlatFormUnMatchedException;
import com.easytrip.backend.exception.impl.SuspendedMemberException;
import com.easytrip.backend.exception.impl.WaitingMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.member.service.sns.KakaoLoginService;
import com.easytrip.backend.member.service.sns.NaverLoginService;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.PlatForm;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MailComponents mailComponents;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final NaverLoginService naverLoginService;
  private final KakaoLoginService kakaoLoginService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate redisTemplate;

  @Override
  @Transactional
  public String signUp(SignUpRequest signUpRequest) {

    // 올바르지 않은 이메일
    if (!isValidEmail(signUpRequest.getEmail())) {
      throw new InvalidEmailException();
    }

    // 비밀번호 체크가 불일치
    if (!signUpRequest.getPassword().equals(signUpRequest.getCheckPassword())) {
      throw new InvalidPasswordConfirmationException();
    }

    // 중복가입인지 확인
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(signUpRequest.getEmail());
    if (byEmail.isPresent()) {
      MemberEntity member = byEmail.get();

      // 탈퇴상태가 아니면 중복가입 exception
      if (!member.getStatus().equals(MemberStatus.WITHDRAWN)) {
        throw new DuplicateEmailException();
      } else {
        // 재가입
        MemberEntity reMember = SignUpRequest.reSignUpInput(member, signUpRequest);
        memberRepository.save(reMember);

        sendMail(signUpRequest, reMember);

        return "가입한 이메일을 확인해 회원인증을 진행해주세요.";
      }
    }

    // 신규가입
    MemberEntity member = SignUpRequest.signUpInput(signUpRequest);
    memberRepository.save(member);

    sendMail(signUpRequest, member);

    return "가입한 이메일을 확인해 회원인증을 진행해주세요.";
  }

  @Override
  @Transactional
  public String auth(String email, String code) {

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    if (member.getAuth()) {
      return "이미 인증을 완료 하셨습니다.";
    }

    if (!code.equals(member.getAuthCode())) {
      throw new InvalidAuthCodeException();
    }

    MemberEntity memberEntity = member.toBuilder()
        .auth(true)
        .status(MemberStatus.ACTIVE)
        .build();
    memberRepository.save(memberEntity);

    return "인증을 완료 했습니다.";
  }

  @Override
  public TokenDto login(LoginRequest loginRequest, PlatForm platForm) {

    MemberEntity member = memberRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new NotFoundMemberException());

    // 가입경로 확인
    if (!member.getPlatForm().equals(platForm)) {
      switch (member.getPlatForm()) {
        case NAVER:
          throw new PlatFormUnMatchedException("NAVER 로 가입한 회원입니다. NAVER 로그인을 이용해주세요.");
        case KAKAO:
          throw new PlatFormUnMatchedException("KAKAO 로 가입한 회원입니다. KAKAO 로그인을 이용해주세요.");
      }
    }

    // 회원 상태에 따른 exception
    if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
      throw new SuspendedMemberException();
    } else if (member.getStatus().equals(MemberStatus.WAITING_FOR_APPROVAL)) {
      throw new WaitingMemberException();
    } else if (member.getStatus().equals(MemberStatus.WITHDRAWN)) {
      throw new NotFoundMemberException();
    }

    // 패스워드 검증
    if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      // 로그인 성공, 토큰 발급
      TokenDto token = tokenService.create(member.getEmail(), member.getAdminYn());
      return token;
    } else {
      // 로그인 실패
      throw new InvalidPasswordException();
    }
  }

  @Override
  @Transactional
  public TokenDto naverLogin(String code, PlatForm platForm) {

    MemberEntity naverMember = naverLoginService.toEntityUser(code, platForm);
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(naverMember.getEmail());

    // 기존에 가입한 회원
    if (byEmail.isPresent()) {
      MemberEntity member = byEmail.get();
      // 가입경로 확인 후 로그인
      if (!member.getPlatForm().equals(platForm)) {
        switch (member.getPlatForm()) {
          case LOCAL:
            throw new PlatFormUnMatchedException("이미 가입한 회원 입니다. 사이트 자체 로그인을 이용해주세요.");
          case KAKAO:
            throw new PlatFormUnMatchedException("KAKAO 로 가입한 회원입니다. KAKAO 로그인을 이용해주세요.");
        }
      }
      TokenDto token = tokenService.create(naverMember.getEmail(), naverMember.getAdminYn());
      return token;
    } else {
      // 새로운 회원(네이버로 회원가입과 동시에 로그인)
      MemberEntity member = naverMember.toBuilder()
          .auth(true)
          .adminYn(false)
          .status(MemberStatus.ACTIVE)
          .regDate(LocalDateTime.now())
          .build();
      memberRepository.save(member);

      TokenDto token = tokenService.create(member.getEmail(), member.getAdminYn());
      return token;
    }
  }

  @Override
  public TokenDto kakaoLogin(String code, PlatForm platForm) {

    MemberEntity kakaoMember = kakaoLoginService.toEntityUser(code, platForm);
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(kakaoMember.getEmail());

    // 기존에 가입한 회원
    if (byEmail.isPresent()) {
      MemberEntity member = byEmail.get();
      // 가입경로 확인 후 로그인
      if (!member.getPlatForm().equals(platForm)) {
        switch (member.getPlatForm()) {
          case LOCAL:
            throw new PlatFormUnMatchedException("이미 가입한 회원 입니다. 사이트 자체 로그인을 이용해주세요.");
          case NAVER:
            throw new PlatFormUnMatchedException("NAVER 로 가입한 회원입니다. NAVER 로그인을 이용해주세요.");
        }
      }
      TokenDto token = tokenService.create(kakaoMember.getEmail(), kakaoMember.getAdminYn());
      return token;
    } else {
      // 새로운 회원(카카오로 회원가입과 동시에 로그인)
      MemberEntity member = kakaoMember.toBuilder()
          .auth(true)
          .adminYn(false)
          .status(MemberStatus.ACTIVE)
          .regDate(LocalDateTime.now())
          .build();
      memberRepository.save(member);

      TokenDto token = tokenService.create(member.getEmail(), member.getAdminYn());
      return token;
    }
  }

  @Override
  public void logout(String accessToken) {

    // 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // 토큰을 통해 사용자 정보 받아오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Redis에 해당 유저의 email로 저장된 refreshToken이 있는지 확인 후 있으면 삭제
    if (redisTemplate.opsForValue().get("RefreshToken: " + authentication.getName()) != null) {
      redisTemplate.delete("RefreshToken: " + authentication.getName());
    }

    // 해당 accessToken 유효시간을 가지고 와서 Redis에 BlackList로 추가
    long expiration = jwtTokenProvider.getExpiration(accessToken);
    long now = (new Date()).getTime();
    long accessTokenExpiresIn = expiration - now;
    redisTemplate.opsForValue()
        .set(accessToken, "logout", accessTokenExpiresIn, TimeUnit.MILLISECONDS);
  }

  private void sendMail(SignUpRequest signUpRequest, MemberEntity member) {
    String email = signUpRequest.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "EZTrip 회원인증 메일";
    String message = "<h3>EZTrip 회원가입에 성공했습니다. 아래의 링크를 클릭하셔서 회원인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/members/auth?email=" + email + "&code="
        + member.getAuthCode() + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);
  }


  private boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
