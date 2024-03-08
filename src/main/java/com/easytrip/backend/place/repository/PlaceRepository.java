package com.easytrip.backend.place.repository;

import com.easytrip.backend.place.domain.PlaceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

  Optional<PlaceEntity> findByAddress(String address);

  Optional<PlaceEntity> findByPlaceId(Long placeId);
}
