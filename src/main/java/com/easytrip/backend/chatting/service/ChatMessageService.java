package com.easytrip.backend.chatting.service;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class ChatMessageService {

    private final SimpMessagingTemplate template;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅메세지
    public void talk(ChatMessageDto.Send message) {


            template.convertAndSend("/sub/chat/" + message.getReceiverId(), message);
            chatMessageRepository.save(message.toChatMessage());


        }


    }

