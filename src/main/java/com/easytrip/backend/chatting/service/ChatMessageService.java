package com.easytrip.backend.chatting.service;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Log4j2
public class ChatMessageService {

    private final RabbitTemplate template;
    private final ChatMessageRepository chatMessageRepository;
    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";

    // 채팅메세지
    @Transactional
    public void talk(ChatMessageDto.Send message, String roomId) {


//            /sub/chat/room/1
            template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomId, message);
            chatMessageRepository.save(message.toChatMessage());


        }


    }

