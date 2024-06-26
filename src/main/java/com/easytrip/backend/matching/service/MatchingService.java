package com.easytrip.backend.matching.service;

import com.easytrip.backend.matching.dto.MatchingMemberDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MatchingService {

  List<MatchingMemberDto> matching(String accessToken);

  void accept(String accessToken, Long memberId);
}
