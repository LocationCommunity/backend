package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordConfirmationException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "PASSWORD_CONFIRM_MISMATCH";
  }

  @Override
  public String getMessage() {
    return "비밀번호와 확인용 비밀번호가 다릅니다.";
  }
}
