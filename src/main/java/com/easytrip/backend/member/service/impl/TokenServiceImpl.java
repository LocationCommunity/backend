package com.easytrip.backend.member.service.impl;

import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.member.service.TokenService;
import com.easytrip.backend.type.Platform;
import io.jsonwebtoken.Claims;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate redisTemplate;
  private final MemberRepository memberRepository;

  @Override
  public TokenDto create(String email, Boolean adminYn, Platform platform) {

    MemberEntity member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundMemberException());
    String nickname = member.getNickname();
    Long memberId = member.getMemberId();

    // AccessToken, RefreshToken 생성
    TokenDto token = jwtTokenProvider.createTokens(email, adminYn, platform, nickname, memberId);

    // Redis 에 RefreshToken 저장
    String refreshToken = token.getRefreshToken();
    long refreshTokenExpiresIn = 86400000;

    redisTemplate.opsForValue()
        .set("RefreshToken: " + email + ", Platform: " + platform, refreshToken, refreshTokenExpiresIn, TimeUnit.MILLISECONDS);

    return token;
  }

  @Override
  public String reissue(String refreshToken) {

    // 토큰이 유효한지 확인
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new InvalidTokenException();
    }

    // Redis에서 해당 사용자의 refreshToken이 있는지 화인하고 있다면 동일한지 확인
    Claims refreshTokenClaims = jwtTokenProvider.getClaimsFromToken(refreshToken);
    String email = refreshTokenClaims.getSubject();
    Platform platform = jwtTokenProvider.getPlatform(refreshToken);

    String storedRefreshToken = (String) redisTemplate.opsForValue().get("RefreshToken: " + email + ", Platform: " + platform);
    if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
      throw new InvalidTokenException();
    }

    // 새로운 accessToken 발급
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    String newAccessToken = jwtTokenProvider.reissue(member.getEmail(), member.getAdminYn(), platform);

    return "accessToke: " + newAccessToken;
  }
}
