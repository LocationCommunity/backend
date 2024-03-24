package com.easytrip.backend.board.controller;

import com.easytrip.backend.board.dto.BoardDetailDto;
import com.easytrip.backend.board.dto.BoardListDto;
import com.easytrip.backend.board.dto.BoardRequestDto;
import com.easytrip.backend.board.dto.PostPlaceDto;
import com.easytrip.backend.board.service.BoardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {

    private BoardService boardService;


    @PostMapping
    public ResponseEntity<String> writePost(@Valid @RequestBody BoardRequestDto boardRequestDto, MultipartFile file, PostPlaceDto postPlaceDto) throws Exception {
        String response = boardService.writePost(boardRequestDto, file, postPlaceDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> updatePost(@Valid @PathVariable(name = "boardId") Long boardId,
                                             @RequestBody BoardRequestDto boardRequestDto, MultipartFile file, PostPlaceDto postPlaceDto) throws Exception {

        String response = boardService.updatePost(boardId, boardRequestDto, file, postPlaceDto);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "boardId") Long boardId) {
        String response = boardService.deletePost(boardId);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/lists/{postId}")
    public ResponseEntity<List<BoardListDto>> getList(@RequestParam(name = "sortByLikes", defaultValue = "false")
                                                      Boolean sortByLikes) {

        List<BoardListDto> response = boardService.getList(sortByLikes);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{postId}")
    public ResponseEntity<BoardDetailDto> getDetail(@PathVariable(name = "boardId") Long boardId) {

        BoardDetailDto response = boardService.getDetail(boardId);

        return ResponseEntity.ok(response);

    }

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
    @GetMapping("/search")
    public ResponseEntity<List<BoardListDto>> search(
            @Valid @NotNull(message = "검색어를 입력해주세요.")
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "searchOption") String searchOption) {

        List<BoardListDto> response = boardService.search(keyword, searchOption);
        return ResponseEntity.ok(response);

    }

}
