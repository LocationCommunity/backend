package com.easytrip.backend.chatting.controller.viewController;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @CrossOrigin // 외부에서 들어오는 js 요청 허용
    @GetMapping("/api/test")
    public String hello() {
        return "테스트입니다.";
    }
}
