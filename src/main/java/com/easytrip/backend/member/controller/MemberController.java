package com.easytrip.backend.member.controller;

import static com.easytrip.backend.type.Platform.KAKAO;
import static com.easytrip.backend.type.Platform.LOCAL;
import static com.easytrip.backend.type.Platform.NAVER;
import com.easytrip.backend.exception.impl.InterestValidationException;
import com.easytrip.backend.member.dto.BookmarkDto;
import com.easytrip.backend.member.dto.MemberDto;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.service.MemberService;
import com.easytrip.backend.type.Interest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/sign-up")
  public void signUp(@Valid @RequestPart(value = "signUpRequest") SignUpRequest signUpRequest,
      @RequestPart(value = "file") MultipartFile file) {
    memberService.signUp(signUpRequest, file, LOCAL);
  }

  @PostMapping("/auth")
  public void auth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code) {
    memberService.auth(email, code, LOCAL);
  }

//  @PostMapping("/login")
//  public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
//    TokenDto response = memberService.login(loginRequest, LOCAL);
//    return ResponseEntity.ok(response);
//  }


  //쿠키 정보로 로그인 유지
  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    TokenDto tokenDto = memberService.login(loginRequest, LOCAL);

    // 토큰을 쿠키에 담아서 클라이언트에게 전달
    Cookie cookie = new Cookie("accessToken", tokenDto.getAccessToken());
    cookie.setHttpOnly(false); // 클라이언트에서 쿠키에 접근하지 못하도록 설정
    // 쿠키를 전송할 도메인 설정 (예: localhost:3000)
    cookie.setDomain("localhost");
    // 쿠키의 유효 시간 설정 (초 단위, 예: 1시간)
    cookie.setMaxAge(3600);
    // 쿠키를 HTTPS 프로토콜로만 전송되도록 설정 (보안을 강화)
    cookie.setSecure(false);
    // 쿠키를 루트 경로에 저장 (모든 경로에서 접근 가능)
    cookie.setPath("/");
    // 응답 헤더에 쿠키 추가
    response.addCookie(cookie);
    log.info("cookie :" + cookie);

    return ResponseEntity.ok(tokenDto);
  }

  @GetMapping("/login/naver")
  public ResponseEntity<TokenDto> naverLogin(@RequestParam(value = "code") String code, HttpServletResponse responser) {
    TokenDto response = memberService.naverLogin(code, NAVER);

    // 토큰을 쿠키에 담아서 클라이언트에게 전달
    Cookie cookie = new Cookie("accessToken", response.getAccessToken());
    cookie.setHttpOnly(false); // 클라이언트에서 쿠키에 접근하지 못하도록 설정
    // 쿠키를 전송할 도메인 설정 (예: localhost:3000)
    cookie.setDomain("localhost");
    // 쿠키의 유효 시간 설정 (초 단위, 예: 1시간)
    cookie.setMaxAge(3600);
    // 쿠키를 HTTPS 프로토콜로만 전송되도록 설정 (보안을 강화)
    cookie.setSecure(false);
    // 쿠키를 루트 경로에 저장 (모든 경로에서 접근 가능)
    cookie.setPath("/");
    // 응답 헤더에 쿠키 추가
    responser.addCookie(cookie);
    log.info("cookie :" + cookie);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/login/kakao")
  public ResponseEntity<TokenDto> kakaoLogin(@RequestParam(name = "code") String code, HttpServletResponse responser) {
    TokenDto response = memberService.kakaoLogin(code, KAKAO);

    // 토큰을 쿠키에 담아서 클라이언트에게 전달
    Cookie cookie = new Cookie("accessToken", response.getAccessToken());
    cookie.setHttpOnly(false); // 클라이언트에서 쿠키에 접근하지 못하도록 설정
    // 쿠키를 전송할 도메인 설정 (예: localhost:3000)
    cookie.setDomain("localhost");
    // 쿠키의 유효 시간 설정 (초 단위, 예: 1시간)
    cookie.setMaxAge(3600);
    // 쿠키를 HTTPS 프로토콜로만 전송되도록 설정 (보안을 강화)
    cookie.setSecure(false);
    // 쿠키를 루트 경로에 저장 (모든 경로에서 접근 가능)
    cookie.setPath("/");
    // 응답 헤더에 쿠키 추가
    responser.addCookie(cookie);
    log.info("cookie :" + cookie);

    return ResponseEntity.ok(response);
  }

  @CrossOrigin
  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = new Cookie("accessToken", null);
    cookie.setHttpOnly(false);
    cookie.setDomain("localhost");
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
    String accessToken = jwtTokenProvider.resolveToken(request);
    memberService.logout(accessToken);
    log.info(accessToken);
    log.info("로그아웃 성공");
  }

  @DeleteMapping("/withdrawal")
  public void withdrawal(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    memberService.withdrawal(accessToken);
  }

  @PutMapping("/password")
  public void resetPassword(@Valid @RequestBody ResetRequest resetRequest) {
    memberService.resetPassword(resetRequest, LOCAL);
  }

  @PostMapping("/password")
  public void passwordAuth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code,
      @RequestParam(name = "resetPassword") String resetPassword) {
    memberService.passwordAuth(email, code, resetPassword, LOCAL);
  }

  @GetMapping("/my-info")
  public ResponseEntity<MemberDto> myInfo(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDto response = memberService.myInfo(accessToken);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/my-info")
  public ResponseEntity<MemberDto> update(HttpServletRequest request,
      @Valid @ModelAttribute UpdateRequest updateRequest, @RequestPart(name = "file") MultipartFile file) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDto response = memberService.update(accessToken, updateRequest, file);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/reissue")
  public ResponseEntity<String> reissue(HttpServletRequest request) {
    String refreshToken = jwtTokenProvider.resolveToken(request);
    String response = memberService.reissue(refreshToken);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/bookmark")
  public ResponseEntity<List<BookmarkDto>> myBookmark(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<BookmarkDto> response = memberService.myBookmark(accessToken);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/bookmark/{bookmarkId}")
  public void bookmarkCancel(HttpServletRequest request,
      @PathVariable Long bookmarkId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    memberService.bookmarkCancel(accessToken, bookmarkId);
  }

  @PostMapping("/interest")
  public void setInterest(HttpServletRequest request, @RequestBody List<Interest> interestList) {
    // interestList 검증 코드
    if (interestList.size() != 3) {
      throw new InterestValidationException();
    }

    for (Interest interest : interestList) {
      if (!Arrays.asList(Interest.values()).contains(interest)) {
        throw new InterestValidationException();
      }
    }

    String accessToken = jwtTokenProvider.resolveToken(request);
    memberService.setInterest(accessToken, interestList);
  }

  @PutMapping("/interest")
  public void changeInterest(HttpServletRequest request, @RequestBody List<Interest> interestList) {
    if (interestList.size() != 3) {
      throw new InterestValidationException();
    }

    for (Interest interest : interestList) {
      if (!Arrays.asList(Interest.values()).contains(interest)) {
        throw new InterestValidationException();
      }
    }

    String accessToken = jwtTokenProvider.resolveToken(request);
    memberService.changeInterest(accessToken, interestList);
  }
}
