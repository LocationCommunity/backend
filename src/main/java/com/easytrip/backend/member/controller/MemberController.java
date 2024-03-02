package com.easytrip.backend.member.controller;

import static com.easytrip.backend.type.PlatForm.LOCAL;
import static com.easytrip.backend.type.PlatForm.NAVER;

import com.easytrip.backend.member.dto.NaverMemberDto;
import com.easytrip.backend.member.dto.NaverMemberDto.NaverMemberDetail;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    String response = memberService.signUp(signUpRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/auth")
  public ResponseEntity<String> auth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code) {
    String response = memberService.auth(email, code);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
    TokenDto response = memberService.login(loginRequest, LOCAL);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/login/naver")
  public ResponseEntity<TokenDto> naverLogin(@RequestParam(name = "code") String code) {
    TokenDto response = memberService.naverLogin(code, NAVER);
    return ResponseEntity.ok(response);
  }

}
