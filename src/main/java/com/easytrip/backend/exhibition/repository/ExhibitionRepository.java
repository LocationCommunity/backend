package com.easytrip.backend.exhibition.repository;

import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {


    Optional<ExhibitionEntity> findByExId(Long exId);

//    List<ExhibitionEntity> findByExIdand


}
