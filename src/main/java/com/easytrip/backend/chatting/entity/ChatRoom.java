package com.easytrip.backend.chatting.entity;
//

import com.easytrip.backend.member.domain.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchedMember1_id")
    private MemberEntity matchedMember1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchedMember2_id")
    private MemberEntity matchedMember2;




    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
//    @OrderBy ( "createdAt DESC" )
    private List<ChatMessage> chatMessageList = new ArrayList<>();



}
