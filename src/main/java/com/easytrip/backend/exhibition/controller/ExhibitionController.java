package com.easytrip.backend.exhibition.controller;


import com.easytrip.backend.exhibition.dto.ExListDto;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;



    //전시회 등록
    @PostMapping()
    public void postExInfo(@RequestPart(value = "boardRequestDto")  ExhibitionDto exhibitionDto,
                                             @RequestPart(value = "files")List<MultipartFile> files
                                             , @RequestPart(value = "placeId") Long placeId)  {

         exhibitionService.postEx(exhibitionDto, files, placeId);



    }


    // 전시회 정보
    @GetMapping("/{exId}")
    public ResponseEntity<ExhibitionDto> getExInfo(@PathVariable("exId") Long exId) {

        ExhibitionDto response = exhibitionService.getEx(exId);


        return ResponseEntity.ok(response);

    }


    // 전시회 정보 수정
    @PutMapping("/{exId}")
    public void updateEx(@PathVariable ("exId") Long exId,
                         @RequestPart(value = "exhibitionDto") ExhibitionDto exhibitionDto,
                         @RequestPart(value = "files") List<MultipartFile> files) {

        exhibitionService.updateEx(exhibitionDto, exId, files);


    }

    // 전시회 정보 삭제
    @DeleteMapping("delete/{exId}")
    public void deleteEx(@PathVariable("exId") Long exId) {

        exhibitionService.deleteEx(exId);
    }

    // 전시회 리스트
    @GetMapping("/lists")
    public List<ExListDto> exList(
            @PageableDefault(page = 0, size = 10, sort = "exId", direction = Sort.Direction.DESC) Pageable pageable) {

        return exhibitionService.exList(pageable);




    }


}
