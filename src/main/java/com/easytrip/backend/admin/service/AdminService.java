package com.easytrip.backend.admin.service;

import com.easytrip.backend.type.MemberStatus;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {

  void setMemberStatus(String accessToken, String memberId, MemberStatus memberStatus);
}
