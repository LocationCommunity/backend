package com.easytrip.backend.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class postController {

    /**
     * 피드페이지
     * @return
     */
    @GetMapping
    public String board(Model model) {



        return "/board";

    }

    /**
     * 게시물 작성
     * @return
     */
    @PostMapping
    public String post() {


        return "/board/post";
    }

    /**
     * 게시물 확인
     * @return
     */
    @GetMapping
    public String postView() {


        return "/board/post/{postId}";
    }


    /**
     * 게시물 삭제
     * @return
     */
    @DeleteMapping
    public String postDelete() {

        return "/board/post/{postId}/delete";
    }


}
