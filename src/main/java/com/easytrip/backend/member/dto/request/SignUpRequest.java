package com.easytrip.backend.member.dto.request;

import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.Platform;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

  @Email(message = "이메일 형식이 올바르지 않습니다.")
  @NotBlank(message = "이메일은 공백일 수 없습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
  private String password;

  @NotBlank(message = "비밀번호 확인은 공백일 수 없습니다.")
  private String checkPassword;

  @NotBlank(message = "이름은 공백일 수 없습니다.")
  private String name;

  @NotBlank(message = "닉네임은 공백일 수 없습니다.")
  private String nickname;
  private String introduction;

  public static MemberEntity signUpInput(MemberEntity member, SignUpRequest signUpRequest,
      ImageEntity image) {
    String uuid = UUID.randomUUID().toString();
    String encPassword = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());

    String imageUrl = (image != null) ? image.getFilePath() : null;

    return member.toBuilder()
        .platform(Platform.LOCAL)
        .email(signUpRequest.getEmail())
        .password(encPassword)
        .name(signUpRequest.getName())
        .nickname(signUpRequest.getNickname())
        .regDate(LocalDateTime.now())
        .auth(false)
        .authCode(uuid)
        .imageUrl(imageUrl)
        .introduction(signUpRequest.getIntroduction())
        .status(MemberStatus.WAITING_FOR_APPROVAL)
        .adminYn(false)
        .build();
  }
}
