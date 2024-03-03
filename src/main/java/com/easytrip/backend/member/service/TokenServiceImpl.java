package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate redisTemplate;

  @Override
  public TokenDto create(String email, Boolean adminYn) {

    // AccessToken, RefreshToken 생성
    TokenDto token = jwtTokenProvider.createTokens(email, adminYn);

    // Redis 에 RefreshToken 저장
    String refreshToken = token.getRefreshToken();
    long refreshTokenExpiresIn = 86400000;

    redisTemplate.opsForValue()
        .set("RefreshToken: " + email, refreshToken, refreshTokenExpiresIn, TimeUnit.MILLISECONDS);

    return token;
  }
}
