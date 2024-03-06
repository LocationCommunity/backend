package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class DuplicatePlaceException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "PLACE_DUPLICATE";
  }

  @Override
  public String getMessage() {
    return "이미 공유된 장소 입니다.";
  }
}
