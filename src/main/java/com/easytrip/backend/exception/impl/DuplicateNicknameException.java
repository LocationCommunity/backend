package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class DuplicateNicknameException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "NICKNAME_DUPLICATE";
  }

  @Override
  public String getMessage() {
    return "이미 존재하는 닉네임 입니다.";
  }
}
