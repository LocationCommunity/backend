package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class WaitingMemberException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "WAITING_MEMBER";
  }

  @Override
  public String getMessage() {
    return "가입대기중인 회원입니다. 가입한 이메일을 통해 회원인증을 진행해주세요.";
  }
}
