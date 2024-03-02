package com.easytrip.backend.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberStatus {

  WAITING_FOR_APPROVAL, // 가입대기 상태
  ACTIVE, // 활동중 상태
  SUSPENDED, // 정지 상태
  WITHDRAWN // 탈퇴 상태
}