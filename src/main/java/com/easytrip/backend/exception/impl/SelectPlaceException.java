package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class SelectPlaceException extends AbstractException {
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return "PLACE_NOT_FOUND";
    }

    @Override
    public String getMessage() {
        return "장소를 선택하지 않았거나, 장소 정보가 없습니다.";
    }
}
