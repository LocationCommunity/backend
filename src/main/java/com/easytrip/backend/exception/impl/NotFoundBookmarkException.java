package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundBookmarkException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return "BOOKMARK_NOT_FOUND";
  }

  @Override
  public String getMessage() {
    return "북마크 정보를 찾을 수 없습니다.";
  }
}
