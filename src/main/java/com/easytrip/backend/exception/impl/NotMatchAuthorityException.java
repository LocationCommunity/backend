package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotMatchAuthorityException extends AbstractException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return "NOT_MATCH_AUTHORITY";
    }

    @Override
    public String getMessage() {
        return "관리자만 등록할 수 있습니다.";
    }
}
