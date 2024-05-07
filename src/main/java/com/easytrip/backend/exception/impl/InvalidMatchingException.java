package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidMatchingException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "INVALID_MATCHING";
  }

  @Override
  public String getMessage() {
    return "잘못된 매칭입니다.";
  }
}
