package com.easytrip.backend.admin.service;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.type.MemberStatus;
import org.springframework.stereotype.Service;

@Service
public interface AdminManagementService {

  void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus);

  MemberDetailDto getMemberInfo(String accessToken, Long memberId);
}
