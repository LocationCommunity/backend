package com.easytrip.backend.chatting.controller;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Log4j2
@Controller
@RequiredArgsConstructor
public class StompChatController {


    private final ChatMessageService chatMessageService;

    // 채팅
    // pub/chat/room
    @MessageMapping("/chat")
    public void Message(ChatMessageDto.Send message) {


        chatMessageService.talk(message);

        log.info(message.getMessage()  + message.getReceiverId() + message.getSenderId() + " - 메시지 전송완료");


    }

}
