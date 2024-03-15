package com.easytrip.backend.member.service;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.MemberDto;
import com.easytrip.backend.member.dto.TokenCreateDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.type.Platform;
import org.springframework.stereotype.Service;

@Service
public interface ManagementService {

  void signUp(SignUpRequest signUpRequest, Platform platForm);

  void auth(String email, String code, Platform platform);

  TokenCreateDto login(LoginRequest loginRequest, Platform platForm);

  TokenCreateDto naverLogin(MemberEntity member, Platform platForm);

  TokenCreateDto kakaoLogin(MemberEntity member, Platform platForm);

  void logout(String accessToken);

  void withdrawal(String accessToken);

  void resetPassword(ResetRequest resetRequest, Platform platform);

  void passwordAuth(String email, String code, String resetPassword, Platform platform);

  MemberDto myInfo(String accessToken);

  MemberDto update(String accessToken, UpdateRequest updateRequest);
}
