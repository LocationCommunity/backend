package com.easytrip.backend.board.exception;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotfoundImageException extends AbstractException {
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return "NOT_FOUND_IMAGE";
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 이미지입니다.";
    }
}
