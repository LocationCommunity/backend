package com.easytrip.backend.place.controller;

import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.place.dto.MapDto;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.place.dto.request.PlaceUpdateRequest;
import com.easytrip.backend.place.service.PlaceService;
import com.easytrip.backend.type.PlaceCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

  private final PlaceService placeService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/share")
  public void placeShare(HttpServletRequest request,
      @Valid @RequestPart(name = "placeRequest") PlaceRequest placeRequest,
      @Valid @NotEmpty(message = "장소의 이미지를 올려주세요.") @RequestPart(name = "file") List<MultipartFile> files) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    placeService.share(accessToken, placeRequest, files);
  }

  @GetMapping("/share")
  public ResponseEntity<List<PlaceDto>> getMyShare(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<PlaceDto> result = placeService.getMyShare(accessToken);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/share/{placeId}")
  public void myShareUpdate(HttpServletRequest request, @Valid @PathVariable Long placeId,
      @Valid @RequestPart(name = "placeUpdateRequest") PlaceUpdateRequest placeUpdateRequest,
      @Valid @NotEmpty(message = "장소의 이미지를 올려주세요.") @RequestPart(name = "file") List<MultipartFile> files) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    placeService.myShareUpdate(accessToken, placeId, placeUpdateRequest, files);
  }

  @GetMapping("/info/{placeId}")
  public ResponseEntity<PlaceDto> getInfo(HttpServletRequest request,
      @PathVariable(value = "placeId") Long placeId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    PlaceDto response = placeService.getInfo(accessToken, placeId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/map")
  public ResponseEntity<List<MapDto>> getMapData(HttpServletRequest request, @RequestParam Double x,
      @RequestParam Double y) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<MapDto> response = placeService.getMapData(accessToken, x, y);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list")
  public ResponseEntity<List<PlaceDto>> placeList(HttpServletRequest request,
      @RequestParam String state,
      @Valid @NotNull(message = "장소의 카테고리는 없을 수 없습니다.") @RequestParam PlaceCategory category) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<PlaceDto> response = placeService.getList(accessToken, state, category);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/info/{placeId}/report")
  public void placeReport(HttpServletRequest request,
      @PathVariable Long placeId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    placeService.report(accessToken, placeId);
  }

  @PostMapping("/info/{placeId}/bookmark")

  public void placeBookmark(HttpServletRequest request,
      @PathVariable Long placeId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    placeService.bookmark(accessToken, placeId);
  }
}
