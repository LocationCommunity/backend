package com.easytrip.backend.exhibition.service.Impl;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.exception.NotfoundImageException;
import com.easytrip.backend.common.image.entity.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.exception.impl.*;
import com.easytrip.backend.exhibition.dto.ExListDto;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.exhibition.repository.ExhibitionRepository;
import com.easytrip.backend.exhibition.service.ExhibitionService;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.place.repository.PlaceRepository;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.ExStatus;
import com.easytrip.backend.type.UseType;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final PlaceRepository placeRepository;


    // 전시회 등록
    @Transactional
    @Override
    public void postEx(ExhibitionDto exhibitionDto, List<MultipartFile> files, Long placeId) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = null;


        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        PlaceEntity place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(NotFoundPlaceException::new);

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_MEMBER"))) {


            throw new NotMatchAuthorityException();
//
        }

        //admin
//
        ExhibitionEntity exhibition = ExhibitionEntity.builder()
                .title(exhibitionDto.getTitle())
                .exName(exhibitionDto.getExName())
                .memberId(member)
                .placdId(place)
                .address(exhibitionDto.getAddress())
                .exInfo(exhibitionDto.getExInfo())
                .exLink(exhibitionDto.getExLink())
                .status(ExStatus.ACTIVE)
                .start_date(LocalDateTime.of(2024, 3, 26, 3, 0))
                .end_date(LocalDateTime.of(2024, 4, 15, 6, 0))
                .regDate(LocalDateTime.now())
                .build();
        exhibitionRepository.save(exhibition);

        for (MultipartFile file : files) {

            // 저장 경로 설정 ~/exhibitions
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\exhibitions\\boards";
            UUID uuid = UUID.randomUUID();


            String fileName = uuid + "_" + file.getOriginalFilename();


            File saveFile = new File(projectPath, fileName);


            try {
                file.transferTo(saveFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            ImageEntity image = ImageEntity.builder()
                    .fileName(fileName)
                    .filePath("/exhibitions/" + fileName)
                    .useType(UseType.EXHIBITION)
                    .build();

            imageRepository.save(image);
        }






    }

    // 전시회 정보 불러오기
    @Override
    public ExhibitionDto getEx(Long exId) {


        ExhibitionEntity exhibitionEntity = exhibitionRepository.findByExId(exId)
                .orElseThrow(NotFoundExhibition::new);


        ExhibitionDto exhibition = ExhibitionDto.builder()
                .title(exhibitionEntity.getTitle())
                .address(exhibitionEntity.getAddress())
                .start_date(LocalDateTime.now())
                .end_date(LocalDateTime.now())
                .exInfo(exhibitionEntity.getExInfo())
                .exLink(exhibitionEntity.getExLink())
                .build();




        return exhibition;

    }


    // 전시회 정보 수정
    @Override
    public void updateEx(ExhibitionDto exhibitionDto, Long exId, List<MultipartFile> files) {

        MemberEntity member = new MemberEntity();
        ExhibitionEntity ex = new ExhibitionEntity();
        ImageEntity image = new ImageEntity();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = null;

        if(authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {




            ex = exhibitionRepository.findByExId(exId).orElseThrow(NotFoundExhibition::new);

            ExhibitionEntity exhibition = ex.toBuilder()
                    .title(exhibitionDto.getTitle())
                    .exName(exhibitionDto.getExName())
                    .exInfo(exhibitionDto.getExInfo())
                    .update_date(LocalDateTime.now())
                    .build();
            exhibitionRepository.save(exhibition);

            for (MultipartFile file : files) {

                // 저장 경로 설정 ~/exhibitions
                String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\exhibitions";
                UUID uuid = UUID.randomUUID();


                String fileName = uuid + "_" + file.getOriginalFilename();


                File saveFile = new File(projectPath, fileName);


                try {
                    file.transferTo(saveFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                imageRepository.findById(exId).orElseThrow(NotfoundImageException::new);

                // 이미지 저장 Board
                ImageEntity imageEntity = image.toBuilder()
                        .fileName(fileName)
                        .filePath("/exhibitions/" + fileName)
                        .build();

                imageRepository.save(imageEntity);
            }


        }

        if (ex.getStatus().equals(ExStatus.INACTIVE)) {
             throw new NotFoundExhibition();

        }


        throw new NotMatchAuthorityException();



        }
    // 전시회 삭제
    @Override
    public void deleteEx(Long exId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MemberEntity member = new MemberEntity();
        ExhibitionEntity exhibition = new ExhibitionEntity();

        String email = null;

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {


            email = authentication.getName();
            exhibition = exhibitionRepository.findByExId(exId).orElseThrow(NotFoundExhibition::new);

            ExhibitionEntity deleteEx = exhibition.toBuilder()
                    .status(ExStatus.INACTIVE)
                    .deleteDate(LocalDateTime.now())
                    .build();

            exhibitionRepository.save(deleteEx);
        }

        throw new NotMatchAuthorityException();



    }


    // 전시회 리스트
    @Override
    public List<ExListDto> exList(Pageable pageable) {
        List<ExhibitionEntity> exhibitions = null;






        return  ExListDto.ListOf(exhibitions);
    }
}










