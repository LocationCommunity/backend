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

  private Long placeId;
  private String nickName;
  private String placeName;
  private String address;
  private List<String> placeImage;
  private String placeInfo;
  private PlaceCategory category;
  private Integer reportCnt;
  private Long bookmarkCnt;
  private Boolean bookmarkYn;
  private Double x;
  private Double y;

  public static List<PlaceDto> listOf(List<PlaceEntity> placeEntities, List<Boolean> bookmarkYnList, List<List<String>> imageUrl) {

    return IntStream.range(0, placeEntities.size())
        .mapToObj(i -> of(placeEntities.get(i), bookmarkYnList.get(i), imageUrl.get(i)))
        .collect(Collectors.toList());
  }

  public static PlaceDto of(PlaceEntity placeEntity, Boolean bookmarkYn, List<String> imageUrl) {

    return PlaceDto.builder()
            .placeId(placeEntity.getPlaceId())
            .nickName(placeEntity.getMemberId().getNickname())
            .placeName(placeEntity.getPlaceName())
            .address(placeEntity.getAddress())
            .x(placeEntity.getX())
            .y(placeEntity.getY())
            .placeImage(imageUrl)
            .placeInfo(placeEntity.getPlaceInfo())
            .category(placeEntity.getCategory())
            .reportCnt(placeEntity.getReportCnt())
            .bookmarkCnt(placeEntity.getBookmarkCnt())
            .bookmarkYn(bookmarkYn)
            .build();
  }
}
