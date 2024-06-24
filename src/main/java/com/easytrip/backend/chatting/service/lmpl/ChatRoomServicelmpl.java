package com.easytrip.backend.chatting.service.lmpl;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.chatting.entity.ChatMessage;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.chatting.entity.MessageReadStatus;
import com.easytrip.backend.chatting.repository.ChatMessageRepository;
import com.easytrip.backend.chatting.repository.ChatRoomRepository;
import com.easytrip.backend.chatting.repository.MessageReadStatusRepository;
import com.easytrip.backend.chatting.service.ChatRoomService;
import com.easytrip.backend.exception.NotFoundRoomException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatRoomServicelmpl implements ChatRoomService {


    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;


    // 채팅방 생성, 참가
    @Transactional
    public Long joinChatRoom(String accessToken, ChatRoomDto.Request join) throws IllegalStateException {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        if (join.getMatchedMember1().equals(join.getMatchedMember2())) {
            throw new IllegalStateException("자신과 채팅방을 개설할 수 없습니다.");
        }

        MemberEntity member1 = memberRepository.findByMemberId(join.getMatchedMember1()).orElseThrow();
        MemberEntity member2 = memberRepository.findByMemberId(join.getMatchedMember2()).orElseThrow();


        Optional<ChatRoom> chatRoom = chatRoomRepository.findByMatchedMember1AndMatchedMember2OrMatchedMember1AndMatchedMember2(member1, member2, member2, member1);
        if (chatRoom.isPresent()) {
            log.info(member1.getEmail() + " 님과 " + member2.getEmail() + "님의 채팅방은 " + "이미 생성된 방입니다.");
            return chatRoom.get().getId();
        } else {
            return chatRoomRepository.save(join.toChatRoom()).getId();
        }

    }




    @Transactional(readOnly = true)
    public List<ChatRoomDto.Response> getRoomList(String accessToken, Long memberId) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        MemberEntity member1 = memberRepository.findAllByMemberId(memberId)
                .orElseThrow(NotFoundMemberException::new);

        List<ChatRoom> chatRooms = chatRoomRepository.findByMatchedMember1OrMatchedMember2(member1, member1);
        List<ChatRoomDto.Response> responses = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
            int unread = getUnreadMessagesCount(chatRoom.getId(), memberId); // 읽지 않은 메시지 수를 가져오는 메서드 호출
            ChatMessage lastMessage = chatMessageRepository.findFirstByChatRoomIdOrderBySendTimeDesc(chatRoom.getId());
            ChatRoomDto.Response response = ChatRoomDto.Response.of(chatRoom);
            response.setUnread(unread);
            if (lastMessage != null) {
                response.setLastMessage(lastMessage.getMessage());
            }
            responses.add(response);
        }

        return responses;
    }




    @Transactional(readOnly = true)
    public ChatRoomDto.Detail getRoomDetail(String accessToken, Long roomId, int page, int size) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Long userId = jwtTokenProvider.getUserId(accessToken);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendTime"));
        Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomId(roomId, pageable);

        List<ChatMessageDto.Response> messages = messagePage.getContent().stream()
                .map(ChatMessageDto.Response::of)
                .collect(Collectors.toList());

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomId);
        ChatRoom chatRoom = optionalChatRoom.orElseThrow(NotFoundRoomException::new);

        int unreadMessagesCount = getUnreadMessagesCount(roomId, userId); // 읽지 않은 메시지 수 가져오기

        ChatRoomDto.Detail detail = ChatRoomDto.Detail.of(chatRoom);
        detail.setMessages(messages);
        detail.setUnread(unreadMessagesCount);

        return detail;


    }

    // 안읽은 메시지 수
    private int getUnreadMessagesCount(Long roomId, Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomId(roomId);

        return (int) messages.stream()
                .filter(message -> {
                    List<MessageReadStatus> statuses = messageReadStatusRepository.findByChatMessageId(message.getId());
                    return statuses.stream().anyMatch(status -> !status.isRead() && status.getUser().getMemberId().equals(userId));
                })
                .count();

    }

    // 메시지 읽음 처리
    @Transactional
    public void markMessageAsRead(String accessToken, Long messageId) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Long memberId = jwtTokenProvider.getUserId(accessToken);

        Optional<ChatMessage> optionalChatMessage = chatMessageRepository.findById(messageId);
        ChatMessage chatMessage = optionalChatMessage.orElseThrow(NotFoundRoomException::new);

        // 사용자가 해당 메시지를 읽었음을 표시
        MessageReadStatus status = messageReadStatusRepository.findByChatMessageIdAndUser(chatMessage.getId(), memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new))
                .orElseGet(() -> {
                    MessageReadStatus newStatus = new MessageReadStatus();
                    newStatus.setChatMessage(chatMessage);
                    newStatus.setUser(memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new));
                    return newStatus;
                });
        status.setRead(true);
        messageReadStatusRepository.save(status);
    }


}
