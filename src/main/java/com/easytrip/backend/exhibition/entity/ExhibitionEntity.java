package com.easytrip.backend.exhibition.entity;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "exhibitions")
public class ExhibitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exId;


    private String title;


    private String address;


    private LocalDateTime start_date;


    private LocalDateTime end_date;


    private LocalDateTime update_date;


    private String exLink;


    private String exInfo;


    @ManyToOne
    @JoinColumn(name = "adminId")
    private MemberEntity memberId;


    @ManyToOne
    @JoinColumn(name = "placeId")
    private PlaceEntity placdId;





}
