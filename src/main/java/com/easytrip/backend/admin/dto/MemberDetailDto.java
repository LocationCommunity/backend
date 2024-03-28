package com.easytrip.backend.admin.dto;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.MemberStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

  public static List<MemberDetailDto> listOf(List<MemberEntity> memberEntities) {
    return memberEntities.stream()
        .map(MemberDetailDto::of)
        .collect(Collectors.toList());
  }

  public static MemberDetailDto of(MemberEntity memberEntity) {

    return MemberDetailDto.builder()
        .memberId(memberEntity.getMemberId())
        .email(memberEntity.getEmail())
        .password(memberEntity.getPassword())
        .name(memberEntity.getPassword())
        .nickname(memberEntity.getNickname())
        .auth(memberEntity.getAuth())
        .imageUrl(memberEntity.getImageUrl())
        .status(memberEntity.getStatus())
        .adminYn(memberEntity.getAdminYn())
        .regDate(memberEntity.getRegDate())
        .build();
  }
}
