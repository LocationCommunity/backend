package com.easytrip.backend.chatting.dto.request;

import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.MemberDto;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;


public class ChatRoomDto {



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long matchedMember1;

        private Long matchedMember2;


        public ChatRoom toChatRoom() {
            return ChatRoom.builder()
                    .matchedMember1(MemberEntity.builder().memberId(matchedMember1).build())
                    .matchedMember2(MemberEntity.builder().memberId(matchedMember2).build())
                    .build();

    }


    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;

        private MemberDto.Response matchedMember1;

        private MemberDto.Response matchedMember2;

        public static Response of(ChatRoom chatRoom) {
            MemberDto.Response matchedMember1 = null, matchedMember2 = null;


            if (chatRoom.getMatchedMember1() != null)
                matchedMember1 = MemberDto.Response.of(chatRoom.getMatchedMember1());
            if (chatRoom.getMatchedMember2() != null)
                matchedMember2 = MemberDto.Response.of(chatRoom.getMatchedMember2());

            return Response.builder()
                    .id(chatRoom.getId())
                    .matchedMember1(matchedMember1)
                    .matchedMember2(matchedMember2)
                    .build();


        }
    }

        @Builder
        @Getter
        @AllArgsConstructor
        public static class Detail {

            private Long id;
            private MemberDto.Response matchedMember1;
            private MemberDto.Response matchedMember2;
            private List<ChatMessageDto.Response> messages;

            public static Detail of(ChatRoom chatRoom) {

                return Detail.builder()
                        .id(chatRoom.getId())
                        .matchedMember1(MemberDto.Response.of(chatRoom.getMatchedMember1()))
                        .matchedMember2(MemberDto.Response.of(chatRoom.getMatchedMember2()))
                        .messages(chatRoom.getChatMessageList().stream().map(ChatMessageDto.Response::of).collect(Collectors.toList()))
                        .build();
            }
        }


    }
