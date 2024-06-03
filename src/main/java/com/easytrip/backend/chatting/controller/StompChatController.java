package com.easytrip.backend.chatting.controller;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.dto.request.NotificationDto;
import com.easytrip.backend.chatting.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;



import java.time.LocalDateTime;


@Log4j2
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final RabbitTemplate rabbitTemplate;
    private final static String CHAT_QUEUE_NAME = "chat.queue";

    private final ChatMessageService chatMessageService;



    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receive(ChatMessageDto.Send message){

        System.out.println("received : " + message.getMessage());
    }
    //Test Tool : apic
    //Subscription URL: /exchange/chat.exchange/room.{roomId}
    //Destination Queue : /pub/chat.talk.{roomId}
    @MessageMapping("chat.talk.{roomId}")
    public void talk(@RequestBody ChatMessageDto.Send message, @DestinationVariable(value = "roomId") String roomId) {

//        message.setSendTime(LocalDateTime.now());
        chatMessageService.talk(message, roomId);

        log.info("메시지 : " + message.getMessage() + " 받는사람 : "+ message.getReceiverId() + " 보낸사람 : "+ message.getSenderId() + " - 메시지 전송완료 ");

    }

    @MessageMapping("/sendNotification/{memberId}")
    public void sendNotification(@DestinationVariable(value = "memberId") Long memberId, NotificationDto notification) {

        notification.setTitle("New Notification");
        notification.setContent("You have a new message!");
        notification.setMemberId(memberId);
        // 여기서 userId에 해당하는 클라이언트에게 알림을 전송
        rabbitTemplate.convertAndSend("chat.exchange" + memberId, notification);
    }

}
