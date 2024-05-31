package com.easytrip.backend.place.dto;

import com.easytrip.backend.place.domain.PlaceEntity;
import java.util.List;
import java.util.stream.Collectors;

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
public class MapDto {

  private Long placeId;
  private Double x;
  private Double y;
  private String placeName;
  private String address;
  private PlaceCategory category;
  private String placeImage;
  private Long bookMarkCnt;

  public static List<MapDto> listOf(List<PlaceEntity> placeEntities) {

    return placeEntities.stream()
        .map(MapDto::of)
        .collect(Collectors.toList());
  }

  public static MapDto of(PlaceEntity placeEntity) {

    return MapDto.builder()
        .placeId(placeEntity.getPlaceId())
            .placeName(placeEntity.getPlaceName())
            .address(placeEntity.getAddress())
            .category(placeEntity.getCategory())
            .bookMarkCnt(placeEntity.getBookmarkCnt())


        .x(placeEntity.getX())
        .y(placeEntity.getY())
        .build();
  }
}
