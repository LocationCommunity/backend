package com.easytrip.backend.member.service;

import com.easytrip.backend.components.MailComponents;
import com.easytrip.backend.exception.impl.DuplicateEmailException;
import com.easytrip.backend.exception.impl.DuplicateNicknameException;
import com.easytrip.backend.exception.impl.InvalidAuthCodeException;
import com.easytrip.backend.exception.impl.InvalidEmailException;
import com.easytrip.backend.exception.impl.InvalidPasswordConfirmationException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.type.MemberStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MailComponents mailComponents;

  @Override
  public String signUp(SignUpRequest signUpRequest) {

    // 올바르지 않은 이메일
    if (isValidEmail(signUpRequest.getEmail())) {
      throw new InvalidEmailException();
    }

    // 비밀번호 체크가 불일치
    if (!signUpRequest.getPassword().equals(signUpRequest.getCheckPassword())) {
      throw new InvalidPasswordConfirmationException();
    }

    // 닉네임 중복
    Optional<MemberEntity> byNickname = memberRepository.findByNickname(
        signUpRequest.getNickname());
    if (byNickname.isPresent()) {
      MemberEntity member = byNickname.get();
      if (member.getStatus().equals(MemberStatus.ACTIVE)) {
        throw new DuplicateNicknameException();
      }
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

  private void sendMail(SignUpRequest signUpRequest, MemberEntity member) {
    String email = signUpRequest.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "EZTrip 회원인증 메일";
    String message = "<h3>EZTrip 회원가입에 성공했습니다. 아래의 링크를 클릭하셔서 회원인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/member/auth?email=" + email + "&code="
        + member.getAuthCode() + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);
  }


  private boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
