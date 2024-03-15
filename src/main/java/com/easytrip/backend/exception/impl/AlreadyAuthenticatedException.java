package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyAuthenticatedException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "ALREADY_AUTHENTICATED";
  }

  @Override
  public String getMessage() {
    return "이미 인증을 완료 했습니다.";
  }
}
