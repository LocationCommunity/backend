package com.easytrip.backend.place.dto.request;

import com.easytrip.backend.type.PlaceCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class PlaceRequest {
  @NotBlank(message = "공유하는 장소의 이름은 공백일 수 없습니다.")
  private String placeName;

  @NotNull(message = "x 좌표는 null 일 수 없습니다.")
  @DecimalMin(value = "33.0", message = "유효하지 않은 x 좌표입니다.")
  @DecimalMax(value = "43.0", message = "유효하지 않은 x 좌표입니다.")
  private Double x;

  @NotNull(message = "y 좌표는 null 일 수 없습니다.")
  @DecimalMin(value = "124.0", message = "유효하지 않은 y 좌표입니다.")
  @DecimalMax(value = "132.0", message = "유효하지 않은 y 좌표입니다.")
  private Double y;

//  @NotBlank(message = "장소의 이미지를 올려주세요.")
//  private String placeImage;

  @NotBlank(message = "장소의 설명을 적어주세요.")
  private String placeInfo;

  @NotNull(message = "장소의 카테고리는 없을 수 없습니다.")
  private PlaceCategory category;
}
