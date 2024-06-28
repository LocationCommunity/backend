package com.easytrip.backend.board.dto;


import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.exception.impl.NotFoundPlaceException;
import com.easytrip.backend.exception.impl.NotFoundPostException;
import com.easytrip.backend.place.domain.PlaceEntity;
import java.util.stream.IntStream;

import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private String memberImage;


    //entity to dto

    public static List<BoardListDto> listOf(List<BoardEntity> boardEntities, List<List< String >> imageUrls, List<String> memberImage) {

//        return boardEntities.stream()
//                .map(BoardListDto::of)
//                .collect(Collectors.toList());

        return IntStream.range(0, boardEntities.size())
                .mapToObj(i -> of(boardEntities.get(i), imageUrls.get(i), memberImage.get(i)))
                .collect(Collectors.toList());



    }


    public static BoardListDto of(BoardEntity boardEntity, List<String>  imageUrl, String memberImage) {

        return BoardListDto.builder()
                .boardId(boardEntity.getBoardId())
                .title(boardEntity.getTitle())
                .boardImage(imageUrl)
                .content(boardEntity.getContent())
                .nickname(boardEntity.getNickname())
                .createDate(boardEntity.getCreateDate())
                .likeCnt(boardEntity.getLikeCnt())
                .viewCnt(boardEntity.getViewCnt())
                .memberImage(memberImage)
                .boardImage(imageUrl)
                .build();

    }





}
