package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.MemberDto;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.ResetRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.type.PlatForm;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  String signUp(SignUpRequest signUpRequest);

  String auth(String email, String code);

  TokenDto login(LoginRequest loginRequest, PlatForm platForm);

  TokenDto naverLogin(String code, PlatForm platForm);

  TokenDto kakaoLogin(String code, PlatForm platForm);

  void logout(String accessToken);

  void withdrawal(String accessToken);

  String resetPassword(ResetRequest resetRequest);

  String passwordAuth(String email, String code, String resetPassword);

  MemberDto myInfo(String accessToken);

  MemberDto update(String accessToken, UpdateRequest updateRequest);
}
