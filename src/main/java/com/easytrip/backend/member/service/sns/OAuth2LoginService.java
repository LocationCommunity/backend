package com.easytrip.backend.member.service.sns;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.Platform;

public interface OAuth2LoginService {

  MemberEntity toEntityUser(String code, Platform platForm);
}
