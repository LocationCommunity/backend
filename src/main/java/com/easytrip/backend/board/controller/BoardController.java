package com.easytrip.backend.board.controller;

import com.easytrip.backend.board.dto.*;
import com.easytrip.backend.board.service.BoardService;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.type.SearchOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")

public class BoardController {

    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/write")
    public void writePost(HttpServletRequest request,
                          @ModelAttribute BoardRequestDto boardRequestDto,
                          @RequestParam(value = "files") List<MultipartFile> files,
                          @RequestParam(value = "placeId", required = false) Long placeId) throws Exception {
        String accessToken = jwtTokenProvider.resolveToken(request);
        boardService.writePost(accessToken, boardRequestDto, files, placeId);
    }



    // 게시물 수정
    @PutMapping("/{boardId}")
    public void updatePost(HttpServletRequest request,
                           @PathVariable("boardId") Long boardId,
                           @RequestPart(value = "placeId", required = false) Long placeId,
                           @RequestPart(value = ("boardRequestDto")) BoardRequestDto boardRequestDto,
                           @RequestPart(value = ("files"), required = false) List<MultipartFile> files) throws Exception {

       String accessToken = jwtTokenProvider.resolveToken(request);

         boardService.updatePost(accessToken, boardId, placeId, boardRequestDto, files);


    }

    // 게시물 삭제 (INACTIVE)
    @DeleteMapping("/delete/{boardId}")
    public void deletePost(HttpServletRequest request,
                           @PathVariable(name = "boardId") Long boardId) {

       String accessToken = jwtTokenProvider.resolveToken(request);

       boardService.deletePost(accessToken, boardId);



    }

//     게시물 목록
    @CrossOrigin
    @GetMapping("/lists")
    public List<BoardListDto> getList(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {


      return boardService.getList(page, size);

    }




//     게시물 불러오기
    @CrossOrigin
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDto> getDetail(@PathVariable(value = "boardId") Long boardId
            , HttpServletRequest request) {

        String accessToken = jwtTokenProvider.resolveToken(request);

        BoardDetailDto response = boardService.getDetail(boardId, accessToken);

        return ResponseEntity.ok(response);

    }


    // 나의 게시물
    @GetMapping("/my-posts")
    public ResponseEntity<List<BoardListDto>> getMyPost(HttpServletRequest request) {

       String accessToken = jwtTokenProvider.resolveToken(request);
        List<BoardListDto> response = boardService.getMyPost(accessToken);
        return ResponseEntity.ok(response);
    }
    // 게시물 좋아요
    @PostMapping("/{boardId}/like")
    public void postLike(HttpServletRequest request, @PathVariable(name = "boardId") Long boardId) {

       String accessToken = jwtTokenProvider.resolveToken(request);

        boardService.likes(boardId, accessToken);
    }


    // 게시글 검색

    @GetMapping("/search")
    public ResponseEntity<List<BoardListDto>> search(
            @Valid @NotNull(message = "검색어를 입력해주세요.")
            @RequestParam(value= "keyword") String keyword,
            @RequestParam(value = "searchOption") SearchOption searchOption
            ) {

        List<BoardListDto> response = boardService.search(keyword, searchOption);
        return ResponseEntity.ok(response);

    }

}
