package com.easytrip.backend.place.dto.request;

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
public class PlaceRequest {

  private String placeName;
  private Double x;
  private Double y;
  private String placeImage;
  private String placeInfo;
  private PlaceCategory category;
}
