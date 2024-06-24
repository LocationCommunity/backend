package com.easytrip.backend.chatting.service.lmpl;

import com.easytrip.backend.chatting.dto.request.ChatMessageDto;
import com.easytrip.backend.chatting.entity.ChatMessage;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.chatting.entity.MessageReadStatus;
import com.easytrip.backend.chatting.repository.ChatMessageRepository;
import com.easytrip.backend.chatting.repository.ChatRoomRepository;
import com.easytrip.backend.chatting.repository.MessageReadStatusRepository;
import com.easytrip.backend.chatting.service.ChatMessageService;
import com.easytrip.backend.exception.NotFoundRoomException;
import com.easytrip.backend.member.domain.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Log4j2
public class ChatMessageServicelmpl implements ChatMessageService {


    private final RabbitTemplate template;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";

    // 채팅메세지
    @Transactional
    public void talk(ChatMessageDto.Send message, String roomId) {
        // RabbitMQ로 메시지 전송
        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomId, message);

        // 메시지 저장
        ChatMessage chatMessage = chatMessageRepository.save(message.toChatMessage());

        // 읽지 않은 상태로 MessageReadStatus 저장
        Long senderId = message.getSenderId();
        List<MemberEntity> participants = getParticipants(Long.parseLong(roomId));

        for (MemberEntity participant : participants) {
            MessageReadStatus readStatus = new MessageReadStatus();
            readStatus.setChatMessage(chatMessage);
            readStatus.setUser(participant);
            readStatus.setRead(participant.getMemberId().equals(senderId)); // 메시지 발신자는 읽음 처리
            messageReadStatusRepository.save(readStatus);
        }
    }

    private List<MemberEntity> getParticipants(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(NotFoundRoomException::new);
        List<MemberEntity> participants = new ArrayList<>();
        participants.add(chatRoom.getMatchedMember1());
        participants.add(chatRoom.getMatchedMember2());
        return participants;
    }


    }

