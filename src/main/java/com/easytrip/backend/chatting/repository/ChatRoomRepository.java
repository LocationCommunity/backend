package com.easytrip.backend.chatting.repository;

import com.easytrip.backend.chatting.entity.ChatRoom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final EntityManager em;

    public Long save(ChatRoom chatRoom) {
        em.persist(chatRoom);
        return chatRoom.getId();
    }

    public Optional<ChatRoom> findById(Long id) {
        return Optional.ofNullable(em.find(ChatRoom.class, id));
    }


    public Optional<ChatRoom> findByMember(Long matchedMember1, Long matchedMember2) {
      return em.createQuery("select r from ChatRoom r where r.matchedMember1.id = :matchedMember1 and r.matchedMember2.id = :matchedMember2", ChatRoom.class)
              .setParameter("matchedMember1", matchedMember1)
              .setParameter("matchedMember2", matchedMember2)
              .getResultList().stream().findFirst();

    }

    public List<ChatRoom> findListByMemberId(Long memberId) {
        return em.createQuery("select r from ChatRoom r where r.matchedMember1.id = : id or r.matchedMember2.id = :id", ChatRoom.class)
                .setParameter("id", memberId)
                .getResultList();
    }
}

