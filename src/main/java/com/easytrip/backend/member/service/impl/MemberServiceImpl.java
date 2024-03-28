package com.easytrip.backend.member.service.impl;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.BookmarkDto;
import com.easytrip.backend.member.dto.MemberDto;
import com.easytrip.backend.member.dto.TokenCreateDto;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.member.service.BookmarkService;
import com.easytrip.backend.member.service.ManagementService;
import com.easytrip.backend.member.service.MemberService;
import com.easytrip.backend.member.service.TokenService;
import com.easytrip.backend.member.service.sns.impl.KakaoLoginServiceImpl;
import com.easytrip.backend.member.service.sns.impl.NaverLoginServiceImpl;
import com.easytrip.backend.type.Platform;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final ManagementService managementService;
  private final TokenService tokenService;
  private final BookmarkService bookmarkService;
  private final NaverLoginServiceImpl naverLoginService;
  private final KakaoLoginServiceImpl kakaoLoginService;

  @Override

  @Transactional
  public void signUp(SignUpRequest signUpRequest, MultipartFile file, Platform platform) {
    managementService.signUp(signUpRequest, file, platform);

  }

  @Override
  public void auth(String email, String code, Platform platform) {
    managementService.auth(email, code, platform);
  }

  @Override
  public TokenDto login(LoginRequest loginRequest, Platform platform) {
    TokenCreateDto createDto = managementService.login(loginRequest, platform);
    TokenDto result = tokenService.create(createDto.getEmail(), createDto.getAdminYn(), platform);
    return result;
  }

  @Override
  public TokenDto naverLogin(String code, Platform platform) {
    MemberEntity member = naverLoginService.toEntityUser(code, platform);
    TokenCreateDto tokenCreateDto = managementService.naverLogin(member, platform);
    TokenDto result = tokenService.create(tokenCreateDto.getEmail(), tokenCreateDto.getAdminYn(),
        platform);
    return result;
  }

  @Override
  public TokenDto kakaoLogin(String code, Platform platform) {
    MemberEntity member = kakaoLoginService.toEntityUser(code, platform);
    TokenCreateDto tokenCreateDto = managementService.kakaoLogin(member, platform);
    TokenDto result = tokenService.create(tokenCreateDto.getEmail(), tokenCreateDto.getAdminYn(),
        platform);
    return result;
  }

  @Override
  public void logout(String accessToken) {
    managementService.logout(accessToken);
  }

  @Override
  public void withdrawal(String accessToken) {
    managementService.withdrawal(accessToken);
  }

  @Override
  public void resetPassword(ResetRequest resetRequest, Platform platform) {
    managementService.resetPassword(resetRequest, platform);
  }

  @Override
  public void passwordAuth(String email, String code, String resetPassword, Platform platform) {
    managementService.passwordAuth(email, code, resetPassword, platform);
  }

  @Override
  public MemberDto myInfo(String accessToken) {
    MemberDto result = managementService.myInfo(accessToken);
    return result;
  }

  @Override

  @Transactional
  public MemberDto update(String accessToken, UpdateRequest updateRequest, MultipartFile file) {
    MemberDto result = managementService.update(accessToken, updateRequest, file);

    return result;
  }

  @Override
  public String reissue(String refreshToken) {
    String result = tokenService.reissue(refreshToken);
    return result;
  }

  @Override
  public List<BookmarkDto> myBookmark(String accessToken) {
    List<BookmarkDto> result = bookmarkService.myBookmark(accessToken);
    return result;
  }

  @Override
  public void bookmarkCancel(String accessToken, Long bookmarkId) {
    bookmarkService.bookmarkCancel(accessToken, bookmarkId);
  }
}
