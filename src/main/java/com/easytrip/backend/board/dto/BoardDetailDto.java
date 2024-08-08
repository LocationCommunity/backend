package com.easytrip.backend.board.dto;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
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

    public static BoardDetailDto getDetail(BoardEntity board, PlaceEntity place, List<String> imageUrls) {

        return BoardDetailDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .images(imageUrls)
                .nickname(board.getNickname())
                .placeId(board.getPlaceId().getPlaceId())
                .placeName(place.getPlaceName())
                .placeLink("http://localhost:8080/place/info/" + place.getPlaceId())
                .visitDate(board.getVisitDate())
                .x(place.getX())
                .y(place.getY())
                .viewCnt(board.getViewCnt() + 1)
                .address(place.getAddress())
                .likeCnt(board.getLikeCnt())
                .build();
    }
}
