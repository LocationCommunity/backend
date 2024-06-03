package com.easytrip.backend.chatting.service;

import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.chatting.repository.ChatRoomRepository;
import com.easytrip.backend.exception.NotFoundRoomException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatRoomService {


    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;


    // 채팅방 생성, 참가
    @Transactional
    public Long joinChatRoom(String accessToken, ChatRoomDto.Request join) throws IllegalStateException {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        if(join.getMatchedMember1().equals(join.getMatchedMember2())) {
            throw new IllegalStateException("자신과 채팅방을 개설할 수 없습니다.");
        }

        MemberEntity member1 = memberRepository.findByMemberId(join.getMatchedMember1()).orElseThrow();
        MemberEntity member2 = memberRepository.findByMemberId(join.getMatchedMember2()).orElseThrow();


        Optional<ChatRoom> chatRoom = chatRoomRepository.findByMatchedMember1AndMatchedMember2OrMatchedMember1AndMatchedMember2(member1, member2, member2, member1);
        if(chatRoom.isPresent()) {
            log.info(member1.getEmail() + " 님과 " + member2.getEmail() +"님의 채팅방은 " + "이미 생성된 방입니다.");
            return chatRoom.get().getId();
        } else {
            return chatRoomRepository.save(join.toChatRoom()).getId();
        }

    }



    // 채팅방 리스트
    @Transactional(readOnly = true)
    public List<ChatRoomDto.Response> getRoomList(String accessToken, Long memberId) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        MemberEntity member1 = memberRepository.findAllByMemberId(memberId).orElseThrow();

        return chatRoomRepository.findByMatchedMember1OrMatchedMember2(member1, member1).stream().map(ChatRoomDto.Response::of).collect(Collectors.toList());


    }

    // 채팅방 디테일
    @Transactional(readOnly = true)
    public ChatRoomDto.Detail getRoomDetail(String accessToken, Long roomId) {


        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }


        Optional<ChatRoomDto.Detail> room = chatRoomRepository.findById(roomId).map(ChatRoomDto.Detail::of);

        return room.orElseThrow(NotFoundRoomException::new);
    }

}
