package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotMyPostException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "NOT_MY_POST";
  }

  @Override
  public String getMessage() {
    return "나의 게시물이 아닙니다.";
  }
}
