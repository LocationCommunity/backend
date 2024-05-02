package com.easytrip.backend.matching.controller;

import com.easytrip.backend.matching.service.MatchingService;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.type.Interest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchingController {

  private final MatchingService matchingService;
  private final JwtTokenProvider jwtTokenProvider;

}
