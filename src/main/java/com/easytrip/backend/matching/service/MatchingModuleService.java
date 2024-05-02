package com.easytrip.backend.matching.service;

import com.easytrip.backend.matching.dto.MatchingMemberDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MatchingModuleService {

  List<MatchingMemberDto> getMatchingList(String accessToken);

  void acceptMatching(String accessToken, Long memberId);
}
