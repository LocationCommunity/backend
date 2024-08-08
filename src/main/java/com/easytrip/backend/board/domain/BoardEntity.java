package com.easytrip.backend.board.domain;



import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.BoardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "boards")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    private String title;

    private String nickname;

    private String content;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private MemberEntity memberId;

    @ManyToOne
    @JoinColumn(name = "placeId")
    private PlaceEntity placeId;

    private Integer likeCnt;

    private Integer viewCnt;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private LocalDateTime deleteDate;

    private String visitDate;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;
}


