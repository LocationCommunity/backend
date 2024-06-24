package com.easytrip.backend.exhibition.entity;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.type.ExCategory;
import com.easytrip.backend.type.ExStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Table(name = "exhibitions")
public class ExhibitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exId;


    private String title;

    private String content;



    private LocalDateTime regDate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    private LocalDateTime deleteDate;

    private LocalDateTime modDate;


    @ManyToOne
    @JoinColumn(name = "adminId")
    private MemberEntity memberId;


    @ManyToOne
    @JoinColumn(name = "placeId")
    private PlaceEntity placdId;



    @Enumerated(EnumType.STRING)
    private ExStatus status;

    private ExCategory exCategory;





}
