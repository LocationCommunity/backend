package com.easytrip.backend.member.service.sns.impl;

import com.easytrip.backend.configuration.KakaoConfiguration;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.KakaoMemberDto;
import com.easytrip.backend.member.dto.KakaoTokenDto;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.member.service.sns.OAuth2LoginService;
import com.easytrip.backend.type.Platform;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLoginServiceImpl implements OAuth2LoginService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final KakaoConfiguration kakaoConfiguration;
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;

  @Override
  public MemberEntity toEntityUser(String code, Platform platForm) {

    String accessToken = toRequestAccessToken(code);
    KakaoMemberDto profile = toRequestProfile(accessToken);

    // 비즈니스로 전환하지 않는 이상 이름 정보는 가져올 수 없어서 일단은 nickname으로 대체
    return MemberEntity.builder()
        .platform(platForm)
        .email(profile.getKakaoAccount().getEmail())
        .name(profile.getKakaoAccount().getProfile().getNickname())
        .nickname(profile.getKakaoAccount().getProfile().getNickname())
        .imageUrl(profile.getKakaoAccount().getProfile().getProfileImageUrl())
        .snsToken(accessToken)
        .build();
  }

  @Override
  public void withdrawl(String accessToken) {
    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, Platform.KAKAO)
        .orElseThrow(() -> new NotFoundMemberException());
    String snsToken = member.getSnsToken();

    // kakao 연결 끊기 API 호출
    String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/unlink";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + snsToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    restTemplate.exchange(kakaoLogoutUrl, HttpMethod.POST, entity, String.class);
  }

  private String toRequestAccessToken(String code) {
    ResponseEntity<KakaoTokenDto> response = restTemplate.exchange(
        kakaoConfiguration.getRequestURL(code), HttpMethod.GET, null, KakaoTokenDto.class);

    return response.getBody().getAccessToken();
  }

  private KakaoMemberDto toRequestProfile(String accessToken) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBearerAuth(accessToken);

    ResponseEntity<KakaoMemberDto> response = restTemplate.postForEntity(
        kakaoConfiguration.getUserInfoUri(), new HttpEntity<>(headers), KakaoMemberDto.class
    );

    return response.getBody();
  }
}
