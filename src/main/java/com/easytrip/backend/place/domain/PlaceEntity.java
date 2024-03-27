package com.easytrip.backend.place.domain;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.PlaceCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
  private String placeInfo;

  @Enumerated(EnumType.STRING)
  private PlaceCategory category;

  private Integer reportCnt;
  private Long bookmarkCnt;
}
