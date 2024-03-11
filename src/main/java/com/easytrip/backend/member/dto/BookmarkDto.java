package com.easytrip.backend.member.dto;

import com.easytrip.backend.place.domain.BookmarkPlaceEntity;
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
public class BookmarkDto {
  private Long placeId;
  private String placeName;
  private String address;

  public static List<BookmarkDto> listOf(List<BookmarkPlaceEntity> bookmarkPlaceEntities) {

    return bookmarkPlaceEntities.stream()
        .map(BookmarkDto::of)
        .collect(Collectors.toList());
  }

  public static BookmarkDto of(BookmarkPlaceEntity bookmarkPlaceEntity) {

    return BookmarkDto.builder()
        .placeId(bookmarkPlaceEntity.getPlaceId().getPlaceId())
        .placeName(bookmarkPlaceEntity.getPlaceId().getPlaceName())
        .address(bookmarkPlaceEntity.getPlaceId().getAddress())
        .build();
  }
}
