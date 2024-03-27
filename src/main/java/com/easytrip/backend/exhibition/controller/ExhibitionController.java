package com.easytrip.backend.exhibition.controller;


import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.exhibition.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/exhibition")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;



    //전시회 등록
    @PostMapping("/")
    public ResponseEntity<ExhibitionEntity> postExInfo(ExhibitionDto exhibitionDto) {

        ExhibitionEntity response = exhibitionService.postExInfo(exhibitionDto);

        return ResponseEntity.ok(response);

    }




    // 전시회 정보
    @GetMapping("/{exId}")
    public ResponseEntity<ExhibitionDto> getExInfo(@PathVariable("exId") Long exId) {

        ExhibitionDto response = exhibitionService.getExInfo(exId);


        return ResponseEntity.ok(response);

    }


    // 전시회 정보 수정
    @PutMapping("/{exId}")
    public void updateEx(@PathVariable ("exId") Long exId,
                         @RequestBody ExhibitionDto exhibitionDto) {

        exhibitionService.updateEx(exhibitionDto, exId);


    }

    // 전시회 정보 삭제


}
