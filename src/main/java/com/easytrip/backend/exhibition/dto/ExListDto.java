package com.easytrip.backend.exhibition.dto;


import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExListDto {

    private String title;

    private String exName;

    private LocalDateTime regDate;


    public static List<ExListDto> ListOf(List<ExhibitionEntity> exhibitionEntities) {

        return exhibitionEntities.stream()
                .map(ExListDto::of)
                .collect(Collectors.toList());
    }

    private static ExListDto of(ExhibitionEntity exhibitionEntity) {

               return ExListDto.builder()
                       .title(exhibitionEntity.getTitle())
                       .exName(exhibitionEntity.getExName())
                       .regDate(LocalDateTime.now())
                       .build();

    }


}
