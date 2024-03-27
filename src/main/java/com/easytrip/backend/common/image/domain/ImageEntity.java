package com.easytrip.backend.common.image.domain
        ;


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

    private String fileName;
    private String filePath;

    @Enumerated(EnumType.STRING)
    private UseType useType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberId;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private PlaceEntity placeId;
}
