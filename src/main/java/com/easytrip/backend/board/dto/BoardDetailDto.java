package com.easytrip.backend.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDetailDto {

    private Long boardId;
    private String title;
    private String content;
    private String nickname;
    private Integer likeCnt;
    private LocalDateTime createDate;

//    private Long placeId;
//
//    private String placeName;
//
//    private String address;
//
//    private Double x;
//
//    private Double y;
//
//    private String placeLink;


}
