package com.easytrip.backend.place.dto;


import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.PlaceCategory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
  private Boolean bookmarkYn;

  public static List<PlaceDto> listOf(List<PlaceEntity> placeEntities, List<Boolean> bookmarkYnList) {

    return IntStream.range(0, placeEntities.size())
        .mapToObj(i -> of(placeEntities.get(i), bookmarkYnList.get(i)))
        .collect(Collectors.toList());

//    return placeEntities.stream()
//        .map(PlaceDto::of)
//        .collect(Collectors.toList());
  }

  public static PlaceDto of(PlaceEntity placeEntity, Boolean bookmarkYn) {

    return PlaceDto.builder()
        .nickName(placeEntity.getMemberId().getNickname())
        .placeName(placeEntity.getPlaceName())
        .address(placeEntity.getAddress())
        .placeImage(placeEntity.getPlaceImage())
        .placeInfo(placeEntity.getPlaceInfo())
        .category(placeEntity.getCategory())
        .reportCnt(placeEntity.getReportCnt())
        .bookmarkCnt(placeEntity.getBookmarkCnt())
        .bookmarkYn(bookmarkYn)
        .build();
  }
}
