package com.easytrip.backend.board.domain;



import com.easytrip.backend.common.image.entity.ImageEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.BoardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
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

    @OneToMany
    @JoinColumn(name = "imageId")
    private List<ImageEntity> imageId;


    private Integer likeCnt;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private LocalDateTime deleteDate;


    @Enumerated(EnumType.STRING)
    private BoardStatus status;






}


