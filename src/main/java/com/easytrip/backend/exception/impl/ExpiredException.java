package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class ExpiredException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "EXPIRE_OVER";
  }

  @Override
  public String getMessage() {
    return "변경기간이 만료되었습니다.";
  }
}
