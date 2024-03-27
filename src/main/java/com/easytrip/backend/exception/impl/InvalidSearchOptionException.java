package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidSearchOptionException extends AbstractException {

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public String getErrorCode() {
        return "POST_NOT_FOUND";
    }


    public String getMessage() {
        return "검색 옵션이 잘못되었습니다.";

    }
}