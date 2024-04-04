package com.easytrip.backend.exhibition.service.Impl;

import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.exception.UnsupportedImageTypeException;
import com.easytrip.backend.exception.impl.*;
import com.easytrip.backend.exhibition.dto.ExListDto;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.exhibition.repository.ExhibitionRepository;
import com.easytrip.backend.exhibition.service.ExhibitionService;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.place.repository.PlaceRepository;
import com.easytrip.backend.type.ExStatus;
import com.easytrip.backend.type.UseType;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final PlaceRepository placeRepository;
    private final JwtTokenProvider  jwtTokenProvider;


    // 전시회 등록
    @Transactional
    @Override
    public void postEx(String accessToken, ExhibitionDto exhibitionDto, List<MultipartFile> files, Long placeId) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();


        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        PlaceEntity place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(SelectPlaceException::new);

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
                .content(exhibitionDto.getContent())
                .memberId(member)
                .placdId(place)
                .status(ExStatus.ACTIVE)
                .start_date(LocalDateTime.of(2024, 3, 26, 3, 0))
                .end_date(LocalDateTime.of(2024, 4, 15, 6, 0))
                .regDate(LocalDateTime.now())
                .build();
        exhibitionRepository.save(exhibition);

        for (MultipartFile file : files) {

            // 저장 경로 설정 ~/exhibitions
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\exhibitions";
            UUID uuid = UUID.randomUUID();


            String fileName = uuid + "_" + file.getOriginalFilename();

            // 파일 이름에서 확장자 추출
            String fileExtension = StringUtils.getFilenameExtension(fileName);


            // 지원하는 이미지 파일 확장자 목록
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");


            // 확장자가 이미지 파일인지 확인
            if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {

                // 빈 껍데기 생성
                File saveFile = new File(projectPath, fileName);

                // transferTo --> Exception 필요
                try {
                    file.transferTo(saveFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                // 이미지 파일이 아닌 경우에 대한 처리
                throw new UnsupportedImageTypeException();
            }


            File saveFile = new File(projectPath, fileName);



            ImageEntity image = ImageEntity.builder()
                    .fileName(fileName)
                    .filePath("/exhibitions/" + fileName)
                    .exId(exhibition)
                    .useType(UseType.EXHIBI)
                    .build();

            imageRepository.save(image);
        }






    }

    // 전시회 정보 불러오기
    @Override
    public ExhibitionDto getEx(Long exId) {



        ExhibitionEntity ex = exhibitionRepository.findByExId(exId).orElseThrow(NotFoundExhibition::new);
        PlaceEntity place = placeRepository.findByPlaceId(ex.getPlacdId().getPlaceId()).orElseThrow(NotFoundPlaceException::new);
        List<ImageEntity> images = imageRepository.findAllByExId(ex);
        List<String> imageUrls = new ArrayList<>();

        for (ImageEntity image : images) {

            imageUrls.add(image.getFilePath());

        }

        ExhibitionDto exhibitionDto = ExhibitionDto.builder()
                .title(ex.getTitle())
                .content(ex.getContent())
                .placeName(place.getPlaceName())
                .placeLink("http://localhost:8080/place/info" + place.getPlaceId())
                .x(place.getX())
                .y(place.getY())
                .address(place.getAddress())
                .start_date(ex.getStart_date())
                .end_date(ex.getEnd_date())
                .build();



        return exhibitionDto;

    }


    // 전시회 정보 수정
    @Transactional
    public void updateEx(String accessToken, ExhibitionDto exhibitionDto, Long exId, Long placeId, List<MultipartFile> files) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ExhibitionEntity ex = new ExhibitionEntity();
        ImageEntity image = new ImageEntity();





        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
            // admin

            ex = exhibitionRepository.findByExId(exId).orElseThrow(SelectPlaceException::new);


        } else {
            throw new NotMatchAuthorityException();
        }



//        have benn deleted post
        if (ex.getStatus().equals(ExStatus.INACTIVE)) {
            throw new DeletePostException();
        }

        if (!files.isEmpty() || files != null) {
            // 기존의 이미지를 삭제하고 새로운 이미지로 대체
            List<ImageEntity> images = imageRepository.findAllByExId(ex);
            imageRepository.deleteAll(images);
        }


        for (MultipartFile file : files) {


            // 저장 경로 설정 ~/boards
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\exhibitions";
            UUID uuid = UUID.randomUUID();

            // 랜덤식별자_원래이름
            String fileName = uuid + "_" + file.getOriginalFilename();


            // 파일 이름에서 확장자 추출
            String fileExtension = StringUtils.getFilenameExtension(fileName);


            // 지원하는 이미지 파일 확장자 목록
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");


            // 확장자가 이미지 파일인지 확인
            if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {

                // 빈 껍데기 생성
                File saveFile = new File(projectPath, fileName);

                // transferTo --> Exception 필요
                try {
                    file.transferTo(saveFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                // 이미지 파일이 아닌 경우에 대한 처리
                throw new UnsupportedImageTypeException();
            }




            // 이미지 저장 Board
            ImageEntity imageEntity = image.toBuilder()
                    .fileName(fileName)
                    .filePath("/exhibitions/" + fileName)
                    .exId(ex)
                    .useType(UseType.EXHIBI)
                    .build();

            imageRepository.save(imageEntity);
        }


        PlaceEntity place = placeRepository.findByPlaceId(placeId).orElseThrow(NotFoundPlaceException::new);
        ExhibitionEntity exhibitionEntity = ex.toBuilder()
                .title(exhibitionDto.getTitle())
                .content(exhibitionDto.getContent())
                .start_date(exhibitionDto.getStart_date())
                .end_date(exhibitionDto.getEnd_date())
                .placdId(place)
                .modDate(LocalDateTime.now())
                .build();
        exhibitionRepository.save(exhibitionEntity);





        }
    // 전시회 삭제
    @Override
    public void deleteEx(String accessToken, Long exId) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MemberEntity member = new MemberEntity();
        ExhibitionEntity exhibition = new ExhibitionEntity();



        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {



            exhibition = exhibitionRepository.findByExId(exId).orElseThrow(NotFoundExhibition::new);

            if (exhibition.getStatus().equals(ExStatus.INACTIVE)) {
                throw new NotFoundExhibition();

            }
            ExhibitionEntity deleteEx = exhibition.toBuilder()
                    .status(ExStatus.INACTIVE)
                    .deleteDate(LocalDateTime.now())
                    .build();

            exhibitionRepository.save(deleteEx);
        }





    }


    // 전시회 리스트
    @Override
    public List<ExListDto> exList(Pageable pageable, String sort) {


//

        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = sort.equals("id") ? "exId" : sort.equals("likes") ? "likeCnt" : null;

        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));
        }

        List<ExhibitionEntity> exhibitions = exhibitionRepository.findAll(pageable).getContent();
        return ExListDto.ListOf(exhibitions);
    }

    }











