package com.easytrip.backend.exhibition.repository;

import com.easytrip.backend.exhibition.dto.ExhibitionDto;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.ExStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {


    Optional<ExhibitionEntity> findByExId(Long exId);


    Optional<ExhibitionEntity> findByExIdAndMemberId(Long exId, MemberEntity member);

    Page<ExhibitionEntity> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(ExStatus exStatus, Date startDate, Date endDate, Pageable pageable);
}
