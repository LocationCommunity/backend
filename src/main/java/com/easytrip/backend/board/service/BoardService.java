package com.easytrip.backend.board.service;

import com.easytrip.backend.board.dto.BoardDetailDto;
import com.easytrip.backend.board.dto.BoardListDto;
import com.easytrip.backend.board.dto.BoardRequestDto;

import com.easytrip.backend.board.dto.PostPlaceDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface BoardService {

    String writePost(BoardRequestDto boardRequestDto, MultipartFile file, PostPlaceDto postPlaceDto) throws Exception;

    String updatePost(Long boardId, BoardRequestDto boardRequestDto, MultipartFile file, PostPlaceDto postPlaceDto) throws Exception;

    String deletePost(Long boardId);

    List<BoardListDto> getList(Boolean sortByLikes);

    BoardDetailDto getDetail(Long boardId);

    List<BoardListDto> getMyPost();

    void likes(Long boardId);

    List<BoardListDto> search(String keyword, String searchOption);

}
