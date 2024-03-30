package com.easytrip.backend.exhibition.controller;


import com.easytrip.backend.exhibition.dto.ExListDto;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.service.ExhibitionService;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;



    //전시회 등록
    @PostMapping()
    public void postExInfo(HttpServletRequest request,
                           @RequestPart(value = "exhibitionDto")  ExhibitionDto exhibitionDto,
                           @RequestPart(value = "files", required = false)List<MultipartFile> files,
                           @RequestPart(value = "placeId", required = false) Long placeId)  {

        String accessToken = jwtTokenProvider.resolveToken(request);

         exhibitionService.postEx(accessToken, exhibitionDto, files, placeId);


    }


    // 전시회 정보
    @GetMapping("/{exId}")
    public ResponseEntity<ExhibitionDto> getExInfo(@PathVariable("exId") Long exId) {

        ExhibitionDto response = exhibitionService.getEx(exId);


        return ResponseEntity.ok(response);

    }


    // 전시회 정보 수정
    @PostMapping("/{exId}")
    public void updateEx(HttpServletRequest request,
                         @PathVariable ("exId") Long exId,
                         @RequestPart(value = "placeId", required = false) Long placeId,
                         @RequestPart(value = "exhibitionDto") ExhibitionDto exhibitionDto,
                         @RequestPart(value = "files", required = false ) List<MultipartFile> files) {

        String accessToken = jwtTokenProvider.resolveToken(request);

        exhibitionService.updateEx(accessToken,exhibitionDto, exId, placeId, files);


    }

    // 전시회 정보 삭제
    @DeleteMapping("delete/{exId}")
    public void deleteEx(HttpServletRequest request, @PathVariable("exId") Long exId) {

        String accessToken = jwtTokenProvider.resolveToken(request);

        exhibitionService.deleteEx(accessToken, exId);
    }

    // 전시회 리스트
    @GetMapping("/lists")
    public List<ExListDto> exList(
            @PageableDefault(page = 0, size = 5) Pageable pageable,
            @RequestParam (value = "sort", required = false) String sort) {

        return exhibitionService.exList(pageable, sort);




    }


}
