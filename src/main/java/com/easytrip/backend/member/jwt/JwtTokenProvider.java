package com.easytrip.backend.member.jwt;


import com.easytrip.backend.member.dto.TokenDto;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.type.Platform;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final Key key;

  // application.yml secret 값 가져와서 key에 저장
  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public TokenDto createTokens(String email, Boolean isAdmin, Platform platForm, String nickname, Long memberId) {

    // AccessToken 클레임 설정
    Claims accessTokenClaims  = Jwts.claims().setSubject(email);
    accessTokenClaims .put("platform", platForm);

    accessTokenClaims .put("nickname", nickname);
    accessTokenClaims .put("memberId", memberId);



    if (isAdmin != null && isAdmin) {
      accessTokenClaims .put("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
    } else {
      accessTokenClaims .put("roles", Collections.singletonList("ROLE_USER"));
    }

    // RefreshToken 클레임 설정
    Claims refreshTokenClaims = Jwts.claims().setSubject(email);
    refreshTokenClaims .put("platform", platForm);

    if (isAdmin != null && isAdmin) {
      refreshTokenClaims .put("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
    } else {
      refreshTokenClaims .put("roles", Collections.singletonList("ROLE_USER"));
    }

    long now = (new Date()).getTime();
    Date accessTokenExpiresIn = new Date(now + 7200000);
    Date refreshTokenExpiresIn = new Date(now + 86400000);



    String accessToken = Jwts.builder()
        .setClaims(accessTokenClaims )
        .setIssuedAt(new Date(now))
        .setExpiration(accessTokenExpiresIn)
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();

    String refreshToken = Jwts.builder()
        .setClaims(refreshTokenClaims)
        .setIssuedAt(new Date(now))
        .setExpiration(refreshTokenExpiresIn)
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();

    TokenDto tokens = TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

    return tokens;
  }

  public String reissue(String email, Boolean isAdmin, Platform platform) {

    Claims claims = Jwts.claims().setSubject(email);

    claims.put("platform", platform);


    if (isAdmin != null && isAdmin) {
      claims.put("roles", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
    } else {
      claims.put("roles", Collections.singletonList("ROLE_USER"));
    }

    long now = (new Date()).getTime();
    Date accessTokenExpiresIn = new Date(now + 3600000);

    String accessToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(now))
        .setExpiration(accessTokenExpiresIn)
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();

    return accessToken;
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaimsFromToken(token);
    String userId = claims.getSubject();
    Collection<? extends GrantedAuthority> authorities = getRolesFromToken(token)
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return new UsernamePasswordAuthenticationToken(userId, null, authorities);
  }

  public String resolveToken(HttpServletRequest request) {
    final String BEARER = "Bearer ";

    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith(BEARER)) {
      token = token.substring(BEARER.length()); // "Bearer " 이후의 토큰 값만 추출
    }
    return token;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(key).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Claims getClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
  }

  public List<String> getRolesFromToken(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.get("roles", List.class);
  }

  public Long getExpiration(String token) {
    Claims claims = getClaimsFromToken(token);
    Date expirationDate = claims.getExpiration();
    return expirationDate.getTime();
  }


  public Platform getPlatform(String token) {
    Claims claimsFromToken = getClaimsFromToken(token);
    String platformString = claimsFromToken.get("platform", String.class);
    return Platform.valueOf(platformString);
  }




    public Long getUserId(String token) {
      try {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        // Assuming you store memberId in claims as a Long
        return claims.get("memberId", Long.class);
      } catch (Exception e) {
        throw new IllegalArgumentException("Invalid token", e);
      }
    }




}
