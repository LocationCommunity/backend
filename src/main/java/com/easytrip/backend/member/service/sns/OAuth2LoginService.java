package com.easytrip.backend.member.service.sns;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.PlatForm;

public interface OAuth2LoginService {

  MemberEntity toEntityUser(String code, PlatForm platForm);
}
