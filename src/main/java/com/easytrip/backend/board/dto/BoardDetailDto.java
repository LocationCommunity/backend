package com.easytrip.backend.board.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDetailDto {

    private Long boardId;

    private String title;

    private String content;

    private List<String> images;

    private String nickname;

    private Integer likeCnt;

    private Integer viewCnt;

    private LocalDateTime createDate;

    private String visitDate;

    private Long placeId;

    private String placeName;

    private String address;

    private Double x;

    private Double y;

    private String placeLink;
}
