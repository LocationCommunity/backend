package com.easytrip.backend.common.image.domain;



import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.UseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "images")
@Entity
public class ImageEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private MemberEntity memberId;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private BoardEntity boardId;

    @ManyToOne
    @JoinColumn(name = "placeId")
    private PlaceEntity placeId;

    @ManyToOne
    @JoinColumn(name = "exId")
    private ExhibitionEntity exId;

    private String fileName;


    private String filePath;


    @Enumerated(EnumType.STRING)
    private UseType useType;







}
