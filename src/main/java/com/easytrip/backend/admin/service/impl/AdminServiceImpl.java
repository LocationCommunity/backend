package com.easytrip.backend.admin.service.impl;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.admin.service.AdminService;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.member.service.ManagementService;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.SearchOption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final ManagementService managementService;

  @Override
  @Transactional
  public void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus) {
    managementService.setMemberStatus(accessToken, memberId, memberStatus);
  }

  @Override
  public MemberDetailDto getMemberInfo(String accessToken, Long memberId) {
    return managementService.getMemberInfo(accessToken, memberId);
  }

  @Override
  @Transactional
  public MemberDetailDto updateMemberInfo(String accessToken, Long memberId,
      UpdateRequest updateRequest, MultipartFile file) {
    return managementService.updateMemberInfo(accessToken, memberId, updateRequest, file);
  }

  @Override
  public List<MemberDetailDto> searchMember(String accessToken, String keyword,
      SearchOption searchOption) {
    return managementService.searchMember(accessToken, keyword, searchOption);
  }
}
