package com.easytrip.backend.exhibition.service;

import com.easytrip.backend.exhibition.dto.ExListDto;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service


public interface ExhibitionService {



    // 전시회 등록
    void postEx(ExhibitionDto exhibitionDto, List<MultipartFile> files, Long placeId);


    // 전시회 정보 불러오기
    ExhibitionDto getEx(Long exId);


    // 전시회 정보 수정
    void updateEx(ExhibitionDto exhibitionDto, Long exId, List<MultipartFile> files);


    // 전시회 정보 삭제
    void deleteEx(Long exId);

    //전시회 리스트
    List<ExListDto> exList(Pageable pageable);









}
