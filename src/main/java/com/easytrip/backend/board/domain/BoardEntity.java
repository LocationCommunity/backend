package com.easytrip.backend.board.domain;

import com.easytrip.backend.board.dto.BoardImageDto;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.BoardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(nullable = false, length = 100)
    private String title;

    private String nickname;

    //    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // Many = Board, One = Member 여러개 게시판에 하나의 유저.
    @JoinColumn(name = "memberId")
    private MemberEntity memberId;

    @ManyToOne
    @JoinColumn(name = "placeId")
    private PlaceEntity placeId;

//    @OneToMany(mappedBy = "boardId", cascade = CascadeType.REMOVE, orphanRemoval = false)
//    private List<ImageEntity> imageEntityList = new ArrayList<>();

    private String fileName;

    private String filePath;


    private Integer likeCnt;

    private LocalDateTime createDate;

    private LocalDateTime modDate;

    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;

}


//    public static BoardEntity toSaveImageEntity(BoardImageDto boardImageDto ) {
//    }
//}
