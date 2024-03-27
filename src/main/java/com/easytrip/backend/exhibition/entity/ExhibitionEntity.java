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
@Table(name = "Exhibitions")
public class ExhibitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exId;

    @Column
    private String title;

    @Column
    private String address;

    @Column
    private LocalDateTime start_date;

    @Column
    private LocalDateTime end_date;

    @Column
    private LocalDateTime update_date;

    @Column
    private String exLink;

    @Column
    private String exInfo;


    @ManyToOne
    @JoinColumn(name = "adminId")
    private MemberEntity memberId;


    @ManyToOne
    @JoinColumn(name = "placeId")
    private PlaceEntity placdId;





}
