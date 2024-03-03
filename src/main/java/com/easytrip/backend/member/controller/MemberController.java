package com.easytrip.backend.member.controller;

import static com.easytrip.backend.type.PlatForm.KAKAO;
import static com.easytrip.backend.type.PlatForm.LOCAL;
import static com.easytrip.backend.type.PlatForm.NAVER;

import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping("/auth")
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

  @GetMapping("/login/kakao")
  public ResponseEntity<TokenDto> kakaoLogin(@RequestParam(name = "code") String code) {
    TokenDto response = memberService.kakaoLogin(code, KAKAO);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String accessToken = getAccessToken(request);
    memberService.logout(accessToken);
    return ResponseEntity.ok("로그아웃 완료");
  }

  @DeleteMapping("/withdrawal")
  public ResponseEntity<String> withdrawal(HttpServletRequest request) {
    String accessToken = getAccessToken(request);
    memberService.withdrawal(accessToken);
    return ResponseEntity.ok("회원탈퇴가 정상적으로 완료되었습니다.");
  }

  @PutMapping("/password")
  public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetRequest resetRequest) {
    String response = memberService.resetPassword(resetRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/password")
  public ResponseEntity<String> passwordAuth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code,
      @RequestParam(name = "resetPassword") String resetPassword) {
    String response = memberService.passwordAuth(email, code, resetPassword);
    return ResponseEntity.ok(response);
  }

  private static String getAccessToken(HttpServletRequest request) {
    String accessToken = request.getHeader("Authorization");
    if (accessToken != null && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7); // "Bearer " 이후의 토큰 값만 추출
    }
    return accessToken;
  }

}
