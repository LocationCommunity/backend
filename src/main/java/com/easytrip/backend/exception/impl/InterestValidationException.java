package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InterestValidationException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "INVALID_INTEREST";
  }

  @Override
  public String getMessage() {
    return "잘못된 관심사 설정입니다.";
  }
}
