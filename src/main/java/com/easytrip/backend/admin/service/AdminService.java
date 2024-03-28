package com.easytrip.backend.admin.service;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.type.MemberStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AdminService {

  void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus);

  MemberDetailDto getMemberInfo(String accessToken, Long memberId);

  MemberDetailDto updateMemberInfo(String accessToken, Long memberId, UpdateRequest updateRequest,
      MultipartFile file);
}
