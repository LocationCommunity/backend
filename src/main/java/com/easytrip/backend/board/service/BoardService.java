package com.easytrip.backend.board.service;

import com.easytrip.backend.board.dto.*;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface BoardService {

    String writePost(BoardRequestDto boardRequestDto,BoardPlaceDto boardPlaceDto, List<MultipartFile> files) throws Exception;

    String updatePost(Long boardId, BoardRequestDto boardRequestDto, List<MultipartFile> files) throws Exception;

    String deletePost(Long boardId);

    List<BoardListDto> getList(Boolean sortByLikes);

    BoardDetailDto getDetail(Long boardId);

    List<BoardListDto> getMyPost();

    void likes(Long boardId);

    List<BoardListDto> search(String keyword, String searchOption);

}
