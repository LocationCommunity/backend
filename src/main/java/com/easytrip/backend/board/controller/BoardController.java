
package com.easytrip.backend.board.controller;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.dto.*;
import com.easytrip.backend.board.service.BoardService;
import com.easytrip.backend.type.SearchOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    // 게시물 작성
    // 작동
    // 이미지 업로드 (MultiPartFile) Rest Api 구현 시 모든 값 RequestPart으로 맵핑 필수
   @PostMapping
    public ResponseEntity<String> writePost(@RequestPart(value = "boardRequestDto") BoardRequestDto boardRequestDto
                                            , @RequestPart(value = "files", required = false) List<MultipartFile> files
                                            , @RequestPart(value = "placeId") Long placeId) throws Exception{
        String response = boardService.writePost(boardRequestDto, files, placeId);

        return ResponseEntity.ok(response);
    }


    // 게시물 수정
    // 작동
    @PostMapping("/{boardId}")
    public ResponseEntity<String> updatePost(@Valid @PathVariable("boardId") Long boardId,
                                             @RequestPart(value = ("boardRequestDto")) BoardRequestDto boardRequestDto,
                                             @RequestPart(value = ("files"), required = false) List<MultipartFile> files) throws Exception {

        String response = boardService.updatePost(boardId, boardRequestDto, files);

        return ResponseEntity.ok(response);
    }

    // 게시물 삭제 (INACTIVE)
    // 작동
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "boardId") Long boardId) {
        String response = boardService.deletePost(boardId);

        return ResponseEntity.ok(response);

    }

    // 게시물 목록
    // 작동
    @GetMapping("/lists")
    public ResponseEntity<List<BoardListDto>> getList(@RequestParam(name = "sortByLikes", defaultValue = "false") Boolean sortByLikes) {

        List<BoardListDto> response = boardService.getList(sortByLikes );

        return ResponseEntity.ok(response);

    }

    // 게시물 불러오기
    // 작동
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDto> getDetail(@PathVariable(value = "boardId") Long boardId, BoardDetailDto boardDetailDto
            ) {

        BoardDetailDto response = boardService.getDetail(boardId, boardDetailDto);

        return ResponseEntity.ok(response);

    }
    // 나의 게시물
    // 작동
    @GetMapping("/my-posts")
    public ResponseEntity<List<BoardListDto>> getMyPost() {
        List<BoardListDto> response = boardService.getMyPost();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/like")
    public void postLike(@PathVariable(name = "boardId") Long boardId) {

        boardService.likes(boardId);
    }


    // 게시글 검색
    // 작동
    // http://localhost:8080/boards/search  { "keyword" : " gogogo " , "searchOption" : " TITLE "  } param
    @GetMapping("/search")
    public ResponseEntity<List<BoardListDto>> search(
            @Valid @NotNull(message = "검색어를 입력해주세요.")
            @RequestParam(value= "keyword") String keyword
            , @RequestParam(value = "searchOption") SearchOption searchOption
            ) {

        List<BoardListDto> response = boardService.search(keyword, searchOption);
        return ResponseEntity.ok(response);

    }

}

