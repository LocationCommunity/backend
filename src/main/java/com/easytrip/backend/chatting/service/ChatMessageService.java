package com.easytrip.backend.chatting.service;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import org.springframework.stereotype.Service;

@Service
public interface ChatMessageService {

     void talk(ChatMessageDto.Send message, String roomId);
}
