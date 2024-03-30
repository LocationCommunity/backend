package com.easytrip.backend.admin.controller;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.admin.service.AdminService;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.SearchOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final JwtTokenProvider jwtTokenProvider;
  private final AdminService adminService;

  @PatchMapping("/members/{memberId}/status")
  public void memberStatus(HttpServletRequest request, @PathVariable Long memberId,
      @RequestParam MemberStatus memberStatus) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    adminService.setMemberStatus(accessToken, memberId, memberStatus);
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<MemberDetailDto> getMemberInfo(HttpServletRequest request,
      @PathVariable Long memberId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDetailDto result = adminService.getMemberInfo(accessToken, memberId);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/members/{memberId}")
  public ResponseEntity<MemberDetailDto> updateMemberInfo(HttpServletRequest request,
      @PathVariable Long memberId, @RequestPart(name = "updateRequest") UpdateRequest updateRequest,
      @RequestPart(name = "file") MultipartFile file) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDetailDto result = adminService.updateMemberInfo(accessToken, memberId,
        updateRequest, file);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/members")
  public ResponseEntity<List<MemberDetailDto>> searchMember(HttpServletRequest request,
      @RequestParam String keyword, @RequestParam SearchOption searchOption) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<MemberDetailDto> result = adminService.searchMember(accessToken, keyword,
        searchOption);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/place/{placeId}")
  public ResponseEntity<PlaceDto> updatePlace(HttpServletRequest request,
      @PathVariable Long placeId, @RequestPart(name = "placeRequest") PlaceRequest placeRequest,
      @Valid @NotEmpty(message = "장소의 이미지를 올려주세요.") @RequestPart(name = "file") List<MultipartFile> files) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    PlaceDto result = adminService.updatePlace(accessToken, placeId, placeRequest, files);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/place/{placeId}")
  public void deletePlace(HttpServletRequest request, @PathVariable Long placeId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    adminService.deletePlace(accessToken, placeId);
  }
}
