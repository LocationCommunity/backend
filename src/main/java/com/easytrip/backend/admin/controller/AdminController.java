package com.easytrip.backend.admin.controller;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.admin.service.AdminService;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.type.MemberStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<MemberDetailDto> getMemberInfo(HttpServletRequest request, @PathVariable Long memberId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDetailDto result = adminService.getMemberInfo(accessToken, memberId);
    return ResponseEntity.ok(result);
  }
}