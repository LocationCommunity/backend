package com.easytrip.backend.matching.dto;

import com.easytrip.backend.member.domain.MemberEntity;
import java.util.List;
import java.util.Set;
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
public class MatchingMemberDto {

  private Long memberId;
  private String nickname;
  private String imageUrl;
  private String introduction;

  public static List<MatchingMemberDto> listOf(Set<MemberEntity> members) {
    return members.stream()
        .map(MatchingMemberDto::of)
        .collect(Collectors.toList());
  }

  public static MatchingMemberDto of(MemberEntity memberEntity) {
    return MatchingMemberDto.builder()
        .memberId(memberEntity.getMemberId())
        .nickname(memberEntity.getNickname())
        .imageUrl(memberEntity.getImageUrl())
        .introduction(memberEntity.getIntroduction())
        .build();
  }
}
