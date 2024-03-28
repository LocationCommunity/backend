package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.BookmarkDto;
import com.easytrip.backend.member.dto.MemberDto;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.type.Platform;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  void signUp(SignUpRequest signUpRequest, Platform platform);

  void auth(String email, String code, Platform platform);

  TokenDto login(LoginRequest loginRequest, Platform platform);

  TokenDto naverLogin(String code, Platform platform);

  TokenDto kakaoLogin(String code, Platform platform);

  void logout(String accessToken);

  void withdrawal(String accessToken);

  void resetPassword(ResetRequest resetRequest, Platform platform);

  void passwordAuth(String email, String code, String resetPassword, Platform platform);

  MemberDto myInfo(String accessToken);

  MemberDto update(String accessToken, UpdateRequest updateRequest);

  String reissue(String refreshToken);

  List<BookmarkDto> myBookmark(String accessToken);

  void bookmarkCancel(String accessToken, Long bookmarkId);
}
