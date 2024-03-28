package com.easytrip.backend.board.dto;


import com.easytrip.backend.board.domain.BoardEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListDto {

    private Long boardId;
    private String title;
    private String nickname;
    private LocalDateTime createDate;
    private Integer likeCnt;


    //entity to dto
    public static List<BoardListDto> listOf(List<BoardEntity> boardEntities) {
        return boardEntities.stream()
                .map(BoardListDto::of)
                .collect(Collectors.toList());

    }

    public static BoardListDto of(BoardEntity boardEntity) {
        return BoardListDto.builder()
                .boardId(boardEntity.getBoardId())
                .title(boardEntity.getTitle())
                .nickname(boardEntity.getNickname())
                .createDate(boardEntity.getCreateDate())
                .likeCnt(boardEntity.getLikeCnt())
                .build();
    }

}
