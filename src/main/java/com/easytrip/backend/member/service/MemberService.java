package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.request.SignUpRequest;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  String signUp(SignUpRequest signUpRequest);

  String auth(String email, String code);
}
