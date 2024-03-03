package com.easytrip.backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@Configuration
@ConfigurationProperties(prefix = "naver")
public class NaverConfiguration {

  private String requestTokenUrl;
  private String clientId;
  private String clientSecret;

  public String getRequestURL(String code) {
    return UriComponentsBuilder.fromHttpUrl(requestTokenUrl)
        .queryParam("grant_type", "authorization_code")
        .queryParam("client_id", clientId)
        .queryParam("client_secret", clientSecret)
        .queryParam("code", code)
        .toUriString();
  }
}
