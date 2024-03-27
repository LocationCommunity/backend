package com.easytrip.backend.admin.service;

import com.easytrip.backend.type.MemberStatus;
import org.springframework.stereotype.Service;

@Service
public interface AdminManagementService {

  void setMemberStatus(String accessToken, String memberId, MemberStatus memberStatus);
}
