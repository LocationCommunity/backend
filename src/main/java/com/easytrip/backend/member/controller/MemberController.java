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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
  public void logout(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    memberService.logout(accessToken);
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
      @Valid @RequestPart(name = "updateRequest") UpdateRequest updateRequest, @RequestPart(name = "file") MultipartFile file) {
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
}
