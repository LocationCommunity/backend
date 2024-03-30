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

    void writePost(String accessToken ,BoardRequestDto boardRequestDto, List<MultipartFile> files, Long placeId) throws Exception;

    void updatePost(String accessToken, Long boardId, Long placeId, BoardRequestDto boardRequestDto, List<MultipartFile> files);

    void deletePost(String accessToken, Long boardId);

    List<BoardListDto> getList( Pageable pageable, String sort);

    BoardDetailDto getDetail( Long boardId);

    List<BoardListDto> getMyPost(String accessToken);

    void likes(Long boardId, String accessToken);

    List<BoardListDto> search(String keyword, SearchOption searchOption);

    List<BoardListDto> searchBoard(String accessToken, String keyword, String searchOption);


}
