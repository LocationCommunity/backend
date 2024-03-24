package com.easytrip.backend.board.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostPlaceDto {


    private String placeName;

    private String address;

    private Double x;

    private Double y;

    private String placeLink;

}
