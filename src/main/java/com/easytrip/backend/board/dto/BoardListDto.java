package com.easytrip.backend.board.dto;


import com.easytrip.backend.board.domain.BoardEntity;
import java.util.stream.IntStream;
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
    private String content;
    private String nickname;
    private LocalDateTime createDate;
    private Integer likeCnt;
    private Integer viewCnt;
    private List<String> boardImage;


    //entity to dto
    public static List<BoardListDto> listOf(List<BoardEntity> boardEntities, List<List<String>> imageUrl) {
        return IntStream.range(0, boardEntities.size())
            .mapToObj(i -> of(boardEntities.get(i), imageUrl.get(i)))
            .collect(Collectors.toList());
    }

    public static BoardListDto of(BoardEntity boardEntity, List<String> imageUrl) {
        return BoardListDto.builder()
                .boardId(boardEntity.getBoardId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .nickname(boardEntity.getNickname())
                .createDate(boardEntity.getCreateDate())
                .likeCnt(boardEntity.getLikeCnt())
                .viewCnt(boardEntity.getViewCnt())
                .boardImage(imageUrl)
                .build();
    }

}
