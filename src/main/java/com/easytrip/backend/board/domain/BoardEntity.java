package com.easytrip.backend.board.domain;

import com.easytrip.backend.member.domain.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "boards")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false, length = 20)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY) // Many = Board, One = Member
    @JoinColumn(name = "memberId")
    private MemberEntity member; // DB는 오브젝트를 저장할 수 있다. FK, 자바는 오브젝트를 저장 할 수 있다.

    @CreationTimestamp
    private Timestamp createDate;



}
