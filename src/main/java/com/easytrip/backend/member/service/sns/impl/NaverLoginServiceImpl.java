package com.easytrip.backend.member.service.sns.impl;

import com.easytrip.backend.configuration.NaverConfiguration;
import com.easytrip.backend.exception.impl.NotFoundPlaceException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.NaverMemberDto;
import com.easytrip.backend.member.dto.NaverTokenDto;
import com.easytrip.backend.member.service.sns.OAuth2LoginService;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.Platform;
import java.lang.reflect.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverLoginServiceImpl implements OAuth2LoginService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final NaverConfiguration naverConfiguration;

  @Override
  public MemberEntity toEntityUser(String code, Platform platForm) {

    String accessToken = toRequestAccessToken(code);
    NaverMemberDto.NaverMemberDetail profile = toRequestProfile(accessToken);

    return MemberEntity.builder()
        .platform(platForm)
        .email(profile.getEmail())
        .name(profile.getName())
        .nickname(profile.getNickname())
        .imageUrl(profile.getImageUrl())
        .build();
  }

  private String toRequestAccessToken(String code) {
    ResponseEntity<NaverTokenDto> response = restTemplate.exchange(
        naverConfiguration.getRequestURL(code), HttpMethod.GET, null, NaverTokenDto.class);

    return response.getBody().getAccessToken();
  }

  private NaverMemberDto.NaverMemberDetail toRequestProfile(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

    ResponseEntity<NaverMemberDto> response = restTemplate.exchange(
        "https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, NaverMemberDto.class);

    return response.getBody().getNaverMemberDetail();
  }

}