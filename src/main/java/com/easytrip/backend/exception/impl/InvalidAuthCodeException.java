package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidAuthCodeException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "AUTH_CODE_INVALID";
  }

  @Override
  public String getMessage() {
    return "유효하지 않는 인증 코드입니다.";
  }
}
