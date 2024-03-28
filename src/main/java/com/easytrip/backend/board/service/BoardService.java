package com.easytrip.backend.board.service;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.dto.*;

import com.easytrip.backend.type.SearchOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface BoardService {

    String writePost(BoardRequestDto boardRequestDto, List<MultipartFile> files, Long placeId) throws Exception;

    String updatePost(Long boardId, BoardRequestDto boardRequestDto, List<MultipartFile> files) throws Exception;

    String deletePost(Long boardId);

    List<BoardListDto> getList(boolean sortByLikes);

    BoardDetailDto getDetail(Long boardId, BoardDetailDto boardDetailDto);

    List<BoardListDto> getMyPost();

    void likes(Long boardId);

    List<BoardListDto> search(String keyword, SearchOption searchOption);

}
