package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class ParsingException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "PARSING_ERROR";
  }

  @Override
  public String getMessage() {
    return "해당 데이터를 파싱할 수 없습니다.";
  }
}
