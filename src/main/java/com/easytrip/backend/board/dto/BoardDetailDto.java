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
    private String nickname;
    private Integer likeCnt;
    private LocalDateTime createDate;


}
