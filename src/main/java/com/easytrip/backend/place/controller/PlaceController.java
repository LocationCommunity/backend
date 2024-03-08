package com.easytrip.backend.place.controller;

import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.place.service.PlaceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

  private final PlaceService placeService;

  @PostMapping("/share")
  public ResponseEntity<?> PlaceShare(HttpServletRequest request,
      @Valid @RequestBody PlaceRequest placeRequest) {
    String accessToken = getToken(request);
    String response = placeService.share(accessToken, placeRequest);
    return ResponseEntity.ok(response);
  }

  private static String getToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // "Bearer " 이후의 토큰 값만 추출
    }
    return token;
  }
}
