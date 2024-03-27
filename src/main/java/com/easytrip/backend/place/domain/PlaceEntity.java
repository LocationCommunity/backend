package com.easytrip.backend.place.domain;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.PlaceCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlaceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long placeId;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;



  private String placeName;
  private String address;
  private Double x;
  private Double y;
  private String placeImage;
  private String placeInfo;

  @Enumerated(EnumType.STRING)
  private PlaceCategory category;

  private Integer reportCnt;
  private Long bookmarkCnt;
}
