package com.easytrip.backend.matching.service.impl;

import com.easytrip.backend.matching.dto.MatchingMemberDto;
import com.easytrip.backend.matching.service.MatchingModuleService;
import com.easytrip.backend.matching.service.MatchingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

  private final MatchingModuleService matchingModuleService;

  @Override
  public List<MatchingMemberDto> matching(String accessToken) {
    return matchingModuleService.getMatchingList(accessToken);
  }

  @Override
  public void accept(String accessToken, Long memberId) {
    matchingModuleService.acceptMatching(accessToken, memberId);
  }
}
