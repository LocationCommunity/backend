package com.easytrip.backend.board.controller;

import com.easytrip.backend.board.service.BoardService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BoardController {

    private BoardService boardService;

    @GetMapping({"", "/"})
    public String index(Model model, @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        model.addAttribute("boards", boardService.getPostList(pageable));
        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() { return "board/saveForm";}

    @GetMapping("/board/{id}")
    public String getPost(Model model, @PathVariable int id) {
        model.addAttribute("boards", boardService.getPost(id));

        return "board/detail";
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(Model model, @PathVariable int id) {
        model.addAttribute("boards", boardService.getPost(id));

        return "board/updateForm";

    }
}
