package com.easytrip.backend.exception.impl;

import com.easytrip.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundExhibition extends AbstractException {


    @Override
    public HttpStatus getHttpStatus() {



        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorCode() {



        return "NOT_FOUND_EXHIBITION";
    }

    @Override
    public String getMessage() {




        return "존재하지 않는 전시회 정보입니다.";
    }
}
