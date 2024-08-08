package com.easytrip.backend.board.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPlaceDto {

    private String placeName;

    private String address;

    private Double x;

    private Double y;

    private String placeLink;
}
