package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.TokenDto;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {

  TokenDto create(String email, Boolean adminYn);

  String reissue(String refreshToken);
}
