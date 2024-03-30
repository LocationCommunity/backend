package com.easytrip.backend.place.dto.request;

import com.easytrip.backend.type.PlaceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PlaceUpdateRequest {

  @NotBlank(message = "공유하는 장소의 이름은 공백일 수 없습니다.")
  private String placeName;

  @NotBlank(message = "장소의 설명을 적어주세요.")
  private String placeInfo;

  @NotNull(message = "장소의 카테고리는 없을 수 없습니다.")
  private PlaceCategory category;
}
