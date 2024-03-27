package com.easytrip.backend.exhibition.service.Impl;

import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundExhibition;
import com.easytrip.backend.exception.impl.NotMatchAuthorityException;
import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.exhibition.repository.ExhibitionRepository;
import com.easytrip.backend.exhibition.service.ExhibitionService;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final MemberRepository memberRepository;





    // 전시회 등록
    @Override
    public String postExInfo(ExhibitionDto exhibitionDto) {


//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        String email = null;
//
//
//        MemberEntity member = memberRepository.findByEmail(email)
//                .orElseThrow(InvalidTokenException::new);
//
//        if (authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(role -> role.equals("ROLE_MEMBER") )) {
//
//
//
//
//
//            throw new NotMatchAuthorityException();
//
//        }
//

        ExhibitionEntity exhibition = ExhibitionEntity.builder()
                .title(exhibitionDto.getTitle())
                .address(exhibitionDto.getAddress())
                .exInfo(exhibitionDto.getExInfo())
                .exLink(exhibitionDto.getExLink())
                .start_date(LocalDateTime.of(2024, 3, 26, 3, 0))
                .end_date(LocalDateTime.of(2024, 4, 15, 6, 0))
                .build();
        exhibitionRepository.save(exhibition);


        return "전시회 정보를 저장 완료하였습니다.";
    }

    // 전시회 정보 불러오기
    @Override
    public ExhibitionDto getExInfo(Long exId) {


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
    public void updateEx(ExhibitionDto exhibitionDto, Long exId) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = null;


        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
            //exhibition exist
            ExhibitionEntity exhibition = exhibitionRepository.findByExId(exId).orElseThrow(NotFoundExhibition::new);

            exhibition.setTitle(exhibitionDto.getTitle());
            exhibition.setAddress(exhibitionDto.getAddress());
            exhibition.setStart_date(LocalDateTime.now());
            exhibition.setEnd_date(LocalDateTime.now());
            exhibition.setExLink(exhibitionDto.getExLink());
            exhibition.setExInfo(exhibitionDto.getExInfo());
            exhibition.setUpdate_date(LocalDateTime.now());
            exhibitionRepository.save(exhibition);


        } else {
            throw new NotMatchAuthorityException();

        }
    }



    // 전시회 정보 삭제
    @Override
    public String deleteEx(Long exId) {
        return null;
    }


}

