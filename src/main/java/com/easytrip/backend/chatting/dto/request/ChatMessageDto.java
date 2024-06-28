package com.easytrip.backend.chatting.dto.request;

import com.easytrip.backend.chatting.entity.ChatMessage;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.MemberDto;
import lombok.*;

import java.time.LocalDateTime;


public class ChatMessageDto {

    @Getter
    @Setter
    public static class Send {

        private String message;

        private Long senderId;

        private Long receiverId;

        private String userNickname;

        private Long roomId;

        private String sendTime;





        public ChatMessage toChatMessage() {
            return ChatMessage.builder()
                    .message(message)
                    .sender(MemberEntity.builder().memberId(senderId).build())
                    .chatRoom(ChatRoom.builder().id(roomId).build())
                    .receiver(MemberEntity.builder().memberId(receiverId).build())
                    .sendTime(LocalDateTime.now())
                    .build();

        }


    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;

        private String message;

        private MemberDto.Response sender;

        private String nickname;

        private LocalDateTime sendTime;



        public static Response of(ChatMessage chatMessage) {

            return Response.builder().message(chatMessage.getMessage())
                    .sender(MemberDto.Response.of(chatMessage.getSender()))
                    .nickname(chatMessage.getSender().getNickname())
                    .sendTime(chatMessage.getSendTime())
                    .id(chatMessage.getId())
                    .build();
        }
    }







}
