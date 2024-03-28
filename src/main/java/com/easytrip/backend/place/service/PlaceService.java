package com.easytrip.backend.place.service;

import com.easytrip.backend.place.dto.MapDto;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.type.PlaceCategory;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface PlaceService {

  void share(String accessToken, PlaceRequest placeRequest, List<MultipartFile> files);

  PlaceDto getInfo(String accessToken, Long placeId);

  List<MapDto> getMapData(String accessToken, Double x, Double y);

  List<PlaceDto> getList(String accessToken, String state, PlaceCategory category);

  void report(String accessToken, Long placeId);

  void bookmark(String accessToken, Long placeId);

  PlaceDto updatePlace(String accessToken, Long placeId, PlaceRequest placeRequest,
      List<MultipartFile> files);

  void deletePlace(String accessToken, Long placeId);
}
