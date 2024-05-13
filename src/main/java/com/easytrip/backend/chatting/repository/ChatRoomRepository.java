package com.easytrip.backend.chatting.repository;

import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.member.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findById (Long id);

    List<ChatRoom> findByMatchedMember1OrMatchedMember2 (MemberEntity matchedMember1, MemberEntity matchedMember2 );

    Optional<ChatRoom> findByMatchedMember1AndMatchedMember2OrMatchedMember1AndMatchedMember2 (MemberEntity matchedMember1, MemberEntity matchedMember2, MemberEntity matchedMember3, MemberEntity matchedMember4);
}


