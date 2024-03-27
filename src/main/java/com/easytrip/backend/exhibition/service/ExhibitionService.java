package com.easytrip.backend.exhibition.service;

import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import org.springframework.stereotype.Service;

@Service


public interface ExhibitionService {



    // 전시회 등록
    ExhibitionEntity postExInfo(ExhibitionDto exhibitionDto);


    // 전시회 정보 불러오기
    ExhibitionDto getExInfo(Long exId);


    // 전시회 정보 수정
    void updateEx(ExhibitionDto exhibitionDto, Long exId);



    // 전시회 정보 삭제
    String deleteEx(Long exId);









}
