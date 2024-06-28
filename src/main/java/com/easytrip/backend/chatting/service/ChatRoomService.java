package com.easytrip.backend.chatting.service;

import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatRoomService {

    Long joinChatRoom(String accessToken, ChatRoomDto.Request join) throws IllegalStateException;

    List<ChatRoomDto.Response> getRoomList(String accessToken, Long memberId);

    ChatRoomDto.Detail getRoomDetail(String accessToken, Long roomId, int page, int size);

    void markMessageAsRead(String accessToken, Long messageId);
}
