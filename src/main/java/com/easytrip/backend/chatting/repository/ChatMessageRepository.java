package com.easytrip.backend.chatting.repository;

import com.easytrip.backend.chatting.entity.ChatMessage;
import com.easytrip.backend.chatting.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    Page<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);
    List<ChatMessage> findByChatRoomId(Long chatRoomId);


    ChatMessage findFirstByChatRoomIdOrderBySendTimeDesc(Long chatRoomId);
}

