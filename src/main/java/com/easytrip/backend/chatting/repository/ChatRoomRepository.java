package com.easytrip.backend.chatting.repository;

import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.member.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
//    private final EntityManager em;

//    public Long save(ChatRoom chatRoom) {
//        em.persist(chatRoom);
//        return chatRoom.getId();
//    }

//    public Optional<ChatRoom> findById(Long id) {
//        return Optional.ofNullable(em.find(ChatRoom.class, id));
//    }


    Optional<ChatRoom> findById (Long id);
//
//
//    public Optional<ChatRoom> findByMember(Long matchedMember1, Long matchedMember2) {
//      return em.createQuery("select r from ChatRoom r where r.matchedMember1.id = :matchedMember1 and r.matchedMember2.id = :matchedMember2", ChatRoom.class)
//              .setParameter("matchedMember1", matchedMember1)
//              .setParameter("matchedMember2", matchedMember2)
//              .getResultList().stream().findFirst();
//
//    }

    Optional<ChatRoom> findByMatchedMember1AndMatchedMember2 (MemberEntity matchedMember1, MemberEntity matchedMember2);

//    public List<ChatRoom> findListByMemberId(Long memberId) {
//        return em.createQuery("select r from ChatRoom r where r.matchedMember1.id = : id or r.matchedMember2.id = :id", ChatRoom.class)
//                .setParameter("id", memberId)
//                .getResultList();
//    }

      List<ChatRoom> findByMatchedMember1OrMatchedMember2 (MemberEntity matchedMember1, MemberEntity matchedMember2 );


//    public Optional<ChatRoom> findByMatchedMembers(Long matchedMember1, Long matchedMember2) {
//        return em.createQuery(
//                        "SELECT r FROM ChatRoom r WHERE (r.matchedMember1.id = :matchedMember1 AND r.matchedMember2.id = :matchedMember2) OR (r.matchedMember1.id = :matchedMember2 AND r.matchedMember2.id = :matchedMember1)",
//                        ChatRoom.class)
//                .setParameter("matchedMember1", matchedMember1)
//                .setParameter("matchedMember2", matchedMember2)
//                .getResultList()
//                .stream()
//                .findFirst();
//    }

    Optional<ChatRoom> findByMatchedMember1AndMatchedMember2OrMatchedMember1AndMatchedMember2 (MemberEntity matchedMember1, MemberEntity matchedMember2, MemberEntity matchedMember3, MemberEntity matchedMember4);
}


