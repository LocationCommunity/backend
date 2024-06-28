package com.easytrip.backend.chatting.repository;

import com.easytrip.backend.chatting.entity.MessageReadStatus;
import com.easytrip.backend.member.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
    List<MessageReadStatus> findByChatMessageId(Long chatMessageId);


    Optional<MessageReadStatus> findByChatMessageIdAndUser(Long id, MemberEntity user);
}