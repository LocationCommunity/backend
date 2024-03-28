package com.easytrip.backend.common.image.repository;


import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<com.easytrip.backend.common.image.entity.ImageEntity, Long> {

  List<ImageEntity> findByPlaceId(PlaceEntity placeId);
}
