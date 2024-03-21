package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.type.Platform;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {

  TokenDto create(String email, Boolean adminYn, Platform platform);

  String reissue(String refreshToken);
}
