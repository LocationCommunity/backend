package com.easytrip.backend.exhibition.service;

import com.easytrip.backend.exhibition.dto.ExListDto;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service


public interface ExhibitionService {



    // 전시회 등록
    void postEx(String accessToken, ExhibitionDto exhibitionDto, List<MultipartFile> files, Long placeId);


    // 전시회 정보 불러오기
    ExhibitionDto getEx(Long exId);


    // 전시회 정보 수정
    void updateEx(String access, ExhibitionDto exhibitionDto, Long exId, Long placeId, List<MultipartFile> files);


    // 전시회 정보 삭제
    void deleteEx(String accessToken, Long exId);

    //전시회 리스트
    List<ExListDto> exList(Date date, int page, int size);









}
