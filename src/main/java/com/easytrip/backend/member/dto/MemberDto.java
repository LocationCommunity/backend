package com.easytrip.backend.member.dto;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.member.domain.MemberEntity;
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
public class MemberDto {

  private String email;
  private String name;
  private String nickname;
  private String imageUrl;
  private String introduction;

  public static MemberDto of(MemberEntity memberEntity) {

    return MemberDto.builder()
        .email(memberEntity.getEmail())
        .name(memberEntity.getName())
        .nickname(memberEntity.getNickname())
        .imageUrl(memberEntity.getImageUrl())
        .introduction(memberEntity.getIntroduction())
        .build();
  }

  @AllArgsConstructor
  @Builder
  @Getter
  public static class Response {
    private Long id;
    private String email;
    private String nickname;




    public static Response of(MemberEntity memberEntity) {
      return Response.builder()
              .id(memberEntity.getMemberId())
              .email(memberEntity.getEmail())
              .nickname(memberEntity.getNickname())
              .build();


    }


  }
}
