package com.easytrip.backend.place.service;

import com.easytrip.backend.place.dto.MapDto;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PlaceService {

  String share(String accessToken, PlaceRequest placeRequest);

  PlaceDto getInfo(String accessToken, Long placeId);

  List<MapDto> getMapData(String accessToken, Double x, Double y);
}
