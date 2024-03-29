package com.easytrip.backend.exhibition.dto;


import com.easytrip.backend.type.UseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExhibitionDto {

    private String title;

    private String Address_ex;

    private LocalDateTime start_date;

    private LocalDateTime end_date;

    private String exName;

    private String exLink;

    private String exInfo;

    private UseType useType;


    private Long placeId;

    private String placeName;

    private String address;

    private Double x;

    private Double y;

    private String placeLink;









    }




