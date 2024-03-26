package com.easytrip.backend.board.dto;

import com.easytrip.backend.board.domain.BoardEntity;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPlaceDto {


    private Long placeId;

    private String placeName;

    private String address;

    private Double x;

    private Double y;

    private String placeLink;


}
