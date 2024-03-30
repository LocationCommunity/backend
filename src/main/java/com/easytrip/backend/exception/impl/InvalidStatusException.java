package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidStatusException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "INVALID_STATUS_SET";
  }

  @Override
  public String getMessage() {
    return "잘못된 상태 설정 입니다.";
  }
}
