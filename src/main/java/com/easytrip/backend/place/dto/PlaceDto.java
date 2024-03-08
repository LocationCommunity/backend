package com.easytrip.backend.place.dto;


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
}
