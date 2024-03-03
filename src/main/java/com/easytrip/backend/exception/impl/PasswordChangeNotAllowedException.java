package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class PasswordChangeNotAllowedException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "PLATFORM_IS_NOT_LOCAL";
  }

  @Override
  public String getMessage() {
    return "소셜 계정으로 회원가입한 회원은 비밀번호 변경이 불가능 합니다.";
  }
}
