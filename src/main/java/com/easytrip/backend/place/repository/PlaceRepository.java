package com.easytrip.backend.place.repository;

import com.easytrip.backend.place.domain.PlaceEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

  Optional<PlaceEntity> findByAddress(String address);

  Optional<PlaceEntity> findByPlaceId(Long placeId);

  // MariaDB에서는 좌표 간 거리를 계산하는 공식인 Haversine formula를 사용
  @Query(value = "SELECT * FROM easytrip.place " +
      "WHERE " +
      "6371 * acos(cos(radians(:y)) * cos(radians(place.y)) * cos(radians(place.x) - radians(:x)) + "
      + "sin(radians(:y)) * sin(radians(place.y))) <= 3", nativeQuery = true)
  List<PlaceEntity> findBySql(@Param("x") Double x, @Param("y") Double y);
}
