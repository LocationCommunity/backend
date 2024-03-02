package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class MailSendException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "SEND_MAIL_FAIL";
  }

  @Override
  public String getMessage() {
    return "메일전송에 실패했습니다.";
  }
}
