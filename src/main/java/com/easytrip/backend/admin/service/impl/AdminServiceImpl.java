package com.easytrip.backend.admin.service.impl;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.admin.service.AdminManagementService;
import com.easytrip.backend.admin.service.AdminService;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final AdminManagementService adminManagementService;

  @Override
  @Transactional
  public void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus) {
    adminManagementService.setMemberStatus(accessToken, memberId, memberStatus);
  }

  @Override
  public MemberDetailDto getMemberInfo(String accessToken, Long memberId) {
    return adminManagementService.getMemberInfo(accessToken, memberId);
  }

  @Override
  @Transactional
  public MemberDetailDto updateMemberInfo(String accessToken, Long memberId,
      UpdateRequest updateRequest, MultipartFile file) {
    return adminManagementService.updateMemberInfo(accessToken, memberId, updateRequest, file);
  }
}
