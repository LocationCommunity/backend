package com.easytrip.backend.admin.service.impl;

import com.easytrip.backend.admin.service.AdminManagementService;
import com.easytrip.backend.admin.service.AdminService;
import com.easytrip.backend.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final AdminManagementService adminManagementService;

  @Override
  @Transactional
  public void setMemberStatus(String accessToken, String memberId, MemberStatus memberStatus) {
    adminManagementService.setMemberStatus(accessToken, memberId, memberStatus);
  }
}
