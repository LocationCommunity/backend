package com.easytrip.backend.member.service.sns.impl;

import com.easytrip.backend.configuration.KakaoConfiguration;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.KakaoMemberDto;
import com.easytrip.backend.member.dto.KakaoTokenDto;
import com.easytrip.backend.member.service.sns.OAuth2LoginService;
import com.easytrip.backend.type.Platform;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLoginServiceImpl implements OAuth2LoginService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final KakaoConfiguration kakaoConfiguration;

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
        .build();
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
