package com.easytrip.backend.member.dto;

import com.easytrip.backend.member.domain.MemberEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.security.cert.CertPathBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverMemberDto {

  @JsonProperty("resultcode")
  private String resultCode;
  @JsonProperty("message")
  private String message;
  @JsonProperty("response")
  private NaverMemberDetail naverMemberDetail;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class NaverMemberDetail {
    private String email;
    private String name;
    private String nickname;
    private String imageUrl;
  }

  public static NaverMemberDto.NaverMemberDetail of(MemberEntity memberEntity) {

    return NaverMemberDetail.builder()
        .email(memberEntity.getEmail())
        .name(memberEntity.getName())
        .nickname(memberEntity.getNickname())
        .imageUrl(memberEntity.getImageUrl())
        .build();
  }

}
