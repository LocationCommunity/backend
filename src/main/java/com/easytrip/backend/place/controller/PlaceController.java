package com.easytrip.backend.place.controller;

import com.easytrip.backend.place.dto.MapDto;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.place.service.PlaceService;
import com.easytrip.backend.type.PlaceCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

  private final PlaceService placeService;

  @PostMapping("/share")
  public ResponseEntity<String> placeShare(HttpServletRequest request,
      @Valid @RequestBody PlaceRequest placeRequest) {
    String accessToken = getToken(request);
    String response = placeService.share(accessToken, placeRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/info/{placeId}")
  public ResponseEntity<PlaceDto> getInfo(HttpServletRequest request,
      @PathVariable Long placeId) {
    String accessToken = getToken(request);
    PlaceDto response = placeService.getInfo(accessToken, placeId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/map")
  public ResponseEntity<List<MapDto>> getMapData(HttpServletRequest request, @RequestParam Double x,
      @RequestParam Double y) {
    String accessToken = getToken(request);
    List<MapDto> response = placeService.getMapData(accessToken, x, y);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list")
  public ResponseEntity<List<PlaceDto>> placeList(HttpServletRequest request,
      @RequestParam String state,
      @Valid @NotNull(message = "장소의 카테고리는 없을 수 없습니다.") @RequestParam PlaceCategory category) {
    String accessToken = getToken(request);
    List<PlaceDto> response = placeService.getList(accessToken, state, category);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/info/{placeId}/report")
  public ResponseEntity<String> placeReport(HttpServletRequest request,
      @PathVariable Long placeId) {
    String accessToken = getToken(request);
    String response = placeService.report(accessToken, placeId);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/info/{placeId}/bookmark")
  public ResponseEntity<String> placeBookmark(HttpServletRequest request,
      @PathVariable Long placeId) {
    String accessToken = getToken(request);
    String response = placeService.bookmark(accessToken, placeId);

    return ResponseEntity.ok(response);
  }

  private static String getToken(HttpServletRequest request) {

    final String BEARER = "Bearer ";

    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith(BEARER)) {
      token = token.substring(BEARER.length()); // "Bearer " 이후의 토큰 값만 추출
    }
    return token;
  }
}
