package com.easytrip.backend.matching.controller;

import com.easytrip.backend.matching.dto.MatchingMemberDto;
import com.easytrip.backend.matching.service.MatchingService;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.type.Interest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
public class MatchingController {

  private final MatchingService matchingService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping
  public ResponseEntity<List<MatchingMemberDto>> getMatchingList(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<MatchingMemberDto> result = matchingService.matching(accessToken);
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public void acceptMatching(HttpServletRequest request, @RequestParam Long memberId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    matchingService.accept(accessToken, memberId);
  }
}
