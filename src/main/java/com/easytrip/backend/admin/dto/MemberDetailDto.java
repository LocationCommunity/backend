package com.easytrip.backend.admin.dto;

import com.easytrip.backend.type.MemberStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetailDto {

  private Long memberId;
  private String email;
  private String password;
  private String name;
  private String nickname;
  private Boolean auth;
  private String imageUrl;
  private String introduction;
  private MemberStatus status;
  private Boolean adminYn;
  private LocalDateTime regDate;
}
