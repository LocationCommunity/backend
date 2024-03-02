package com.easytrip.backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoConfiguration {

  private String tokenUri;
  private String userInfoUri;
  private String grantType;
  private String clientId;
  private String redirectUri;

  public String getRequestURL(String code) {
    return UriComponentsBuilder.fromHttpUrl(tokenUri)
        .queryParam("grant_type", grantType)
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("code", code)
        .toUriString();
  }
}
