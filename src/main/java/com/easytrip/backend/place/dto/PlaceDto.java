package com.easytrip.backend.place.dto;


import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.PlaceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {

  private String nickName;
  private String placeName;
  private String address;
  private String placeImage;
  private String placeInfo;
  private PlaceCategory category;
  private Integer reportCnt;
  private Long bookmarkCnt;


  public static PlaceDto of(PlaceEntity placeEntity) {

    return PlaceDto.builder()
        .nickName(placeEntity.getMemberId().getNickname())
        .placeName(placeEntity.getPlaceName())
        .address(placeEntity.getAddress())
        .placeImage(placeEntity.getPlaceImage())
        .placeInfo(placeEntity.getPlaceInfo())
        .category(placeEntity.getCategory())
        .reportCnt(placeEntity.getReportCnt())
        .bookmarkCnt(placeEntity.getBookmarkCnt())
        .build();
  }
}
