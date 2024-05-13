package com.easytrip.backend.chatting.service;

import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.chatting.repository.ChatRoomRepository;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;


    // 채팅방 생성, 참가
    @Transactional
    public Long joinChatRoom(ChatRoomDto.Request join) throws IllegalStateException {

        if(join.getMatchedMember1().equals(join.getMatchedMember2())) {
            throw new IllegalStateException("자신과 채팅방을 개설할 수 없습니다.");
        }

        MemberEntity member1 = memberRepository.findByMemberId(join.getMatchedMember1()).orElseThrow();
        MemberEntity member2 = memberRepository.findByMemberId(join.getMatchedMember2()).orElseThrow();


        Optional<ChatRoom> chatRoom = chatRoomRepository.findByMatchedMember1AndMatchedMember2(member1, member2);
        if(chatRoom.isPresent()) {
            return chatRoom.get().getId();
        } else {
            return chatRoomRepository.save(join.toChatRoom()).getId();
        }

    }



    // 채팅방 리스트
    @Transactional(readOnly = true)
    public List<ChatRoomDto.Response> getRoomList(Long memberId) {
        MemberEntity member1 = memberRepository.findAllByMemberId(memberId).orElseThrow();

        return chatRoomRepository.findByMatchedMember1OrMatchedMember2(member1, member1).stream().map(ChatRoomDto.Response::of).collect(Collectors.toList());


    }

    // 채팅방 디테일
    @Transactional(readOnly = true)
    public ChatRoomDto.Detail getRoomDetail(Long roomId) {

        Optional<ChatRoomDto.Detail> room = chatRoomRepository.findById(roomId).map(ChatRoomDto.Detail::of);

        return room.orElseThrow();
    }

}
