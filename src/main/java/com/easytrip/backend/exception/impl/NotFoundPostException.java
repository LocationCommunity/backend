package com.easytrip.backend.exception.impl;

import org.springframework.http.HttpStatus;

public class NotFoundPostException extends AbstractMethodError{

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public String getErrorCode() {
        return "POST_NOT_FOUND";
    }


    public String getMessage() {
        return "존재하지 않는 게시물입니다.";
    }


}
