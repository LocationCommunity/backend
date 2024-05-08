package com.easytrip.backend.exception;

import org.springframework.http.HttpStatus;

public class NotFoundRoomException extends AbstractException {
    @Override
    public HttpStatus getHttpStatus() {


        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return "NOT_FOUND_CHATROOM";
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 채팅방입니다.";

    }
}