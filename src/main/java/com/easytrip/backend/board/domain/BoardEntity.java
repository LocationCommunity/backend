package com.easytrip.backend.board.domain;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.BoardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "board")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(nullable = false, length = 100)
    private String title;

    private String nickname;

    private String fileName;

    private String filePath;

//    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // Many = Board, One = Member
    @JoinColumn(name = "memberId")
    private MemberEntity memberId; // DB는 오브젝트를 저장할 수 있다. FK, 자바는 오브젝트를 저장 할 수 있다.


    private Integer likeCnt;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;



}
