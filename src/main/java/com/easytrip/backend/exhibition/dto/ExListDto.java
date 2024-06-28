package com.easytrip.backend.exhibition.dto;


import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.type.ExCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExListDto {

    private Long exId;

    private String title;

    private Date startDate;

    private Date endDate;

    private ExCategory exCategory;

    private List<String> exImage;

    private String memberImage;

    private LocalDateTime regDate;


    public static List<ExListDto> listOf(List<ExhibitionEntity> exhibitionEntities, List<List<String>> imageUrls, List<String> memberImage) {

        return IntStream.range(0, exhibitionEntities.size())
                .mapToObj(i -> of(exhibitionEntities.get(i), imageUrls.get(i), memberImage.get(i)))
                .collect(Collectors.toList());
    }

    private static ExListDto of(ExhibitionEntity exhibitionEntity, List<String> imageUrl, String memberImage) {

               return ExListDto.builder()
                       .exId(exhibitionEntity.getExId())
                       .title(exhibitionEntity.getTitle())
                       .exCategory(exhibitionEntity.getExCategory())
                       .startDate(exhibitionEntity.getStartDate())
                       .endDate(exhibitionEntity.getEndDate())
                       .regDate(LocalDateTime.now())
                       .exImage(imageUrl)
                       .memberImage(memberImage)
                       .build();

    }


}
