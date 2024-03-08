package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundPlaceException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return "PLACE_NOT_FOUND";
  }

  @Override
  public String getMessage() {
    return "존재하지 않는 장소입니다.";
  }
}
