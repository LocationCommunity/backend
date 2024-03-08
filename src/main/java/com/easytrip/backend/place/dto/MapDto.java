package com.easytrip.backend.place.dto;

import com.easytrip.backend.place.domain.PlaceEntity;
import java.util.List;
import java.util.stream.Collectors;
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

  public static List<MapDto> listOf(List<PlaceEntity> placeEntities) {

    return placeEntities.stream()
        .map(MapDto::of)
        .collect(Collectors.toList());
  }

  public static MapDto of(PlaceEntity placeEntity) {

    return MapDto.builder()
        .placeId(placeEntity.getPlaceId())
        .x(placeEntity.getX())
        .y(placeEntity.getY())
        .build();
  }
}
