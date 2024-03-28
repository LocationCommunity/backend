package com.easytrip.backend.common.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<com.easytrip.backend.common.image.entity.ImageEntity, Long> {

}
