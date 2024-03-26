package com.easytrip.backend.board.controller;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.dto.*;
import com.easytrip.backend.board.service.BoardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

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

    // 작동
    // 이미지 업로드 (MultiPartFile) Rest Api 구현 시 모든 값 RequestPart으로 맵핑 필수
   @PostMapping
    public ResponseEntity<String> writePost(@RequestPart(value = "boardRequestDto") BoardRequestDto boardRequestDto,
                                            @RequestPart(value = "boardPlaceDto") BoardPlaceDto boardPlaceDto
                                            , @RequestPart(value = "files") List<MultipartFile> files) throws Exception{
        String response = boardService.writePost(boardRequestDto, boardPlaceDto, files);

        return ResponseEntity.ok(response);
    }

    // 작동
    @PostMapping("/{boardId}")
    public ResponseEntity<String> updatePost(@Valid @PathVariable("boardId") Long boardId,
                                             @RequestPart(value = ("boardRequestDto")) BoardRequestDto boardRequestDto,
                                             @RequestPart(value = ("files")) List<MultipartFile> files) throws Exception {

        String response = boardService.updatePost(boardId, boardRequestDto, files);

        return ResponseEntity.ok(response);
    }


    // 작동
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "boardId") Long boardId) {
        String response = boardService.deletePost(boardId);

        return ResponseEntity.ok(response);

    }
    // 작동
    @GetMapping("/lists")
    public ResponseEntity<List<BoardListDto>> getList(@RequestParam(name = "sortByLikes", defaultValue = "false")
                                                      Boolean sortByLikes) {

        List<BoardListDto> response = boardService.getList(sortByLikes);

        return ResponseEntity.ok(response);

    }


    // 작동
    @GetMapping("/{boardId}")
    public ResponseEntity<Optional<BoardEntity>> getDetail(@PathVariable(value = "boardId") Long boardId, BoardDetailDto boardDetailDto
            ) {

        Optional<BoardEntity> response = boardService.getDetail(boardId, boardDetailDto);

        return ResponseEntity.ok(response);

    }
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

    /**
     * 게시글 검색
     * @param keyword
     * @param searchOption
     * @return
     */

    // 작동 안됨 keyword is not present
    // http://localhost:8080/boards/search  { "keyword" : " asd " , "searchOption" : " TITLE "  }  json = error
    @GetMapping("/search")
    public ResponseEntity<List<BoardListDto>> search(
            @Valid @NotNull(message = "검색어를 입력해주세요.")
            @RequestParam(value= "keyword") String keyword,
            @RequestParam(value= "searchOption") String searchOption) {

        List<BoardListDto> response = boardService.search(keyword, searchOption);
        return ResponseEntity.ok(response);

    }

}
