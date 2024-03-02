package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class PlatFormUnMatchedException extends AbstractException {

  private final String message;

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "PLATFORM_UNMATCHED";
  }

  @Override
  public String getMessage() {
    return message;
  }
}
