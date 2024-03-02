package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.dto.request.LoginRequest;
import com.easytrip.backend.member.dto.request.SignUpRequest;
import com.easytrip.backend.type.PlatForm;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  String signUp(SignUpRequest signUpRequest);

  String auth(String email, String code);

  TokenDto login(LoginRequest loginRequest, PlatForm platForm);

  TokenDto naverLogin(String code, PlatForm platForm);
}
