package com.easytrip.backend.place.service.impl;

import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.exception.impl.DuplicatePlaceException;
import com.easytrip.backend.exception.impl.ImageSaveException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.exception.impl.NotFoundPlaceException;
import com.easytrip.backend.exception.impl.ParsingException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.place.domain.BookmarkPlaceEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.place.dto.MapDto;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.place.repository.BookmarkPlaceRepository;
import com.easytrip.backend.place.repository.PlaceRepository;
import com.easytrip.backend.place.service.PlaceService;
import com.easytrip.backend.type.PlaceCategory;
import com.easytrip.backend.type.UseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final MemberRepository memberRepository;
  private final PlaceRepository placeRepository;
  private final BookmarkPlaceRepository bookmarkPlaceRepository;
  private final ImageRepository imageRepository;

  @Override
  @Transactional
  public void share(String accessToken, PlaceRequest placeRequest, List<MultipartFile> files) {

    // 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // 토큰을 통해 사용자 정보 받아오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    // 받은 위경도를 주소로 변경
    String address = getAddress(placeRequest.getX(), placeRequest.getY());

    // 주소를 통해 이미 저장되어 있는 장소인지 확인
    Optional<PlaceEntity> byAddress = placeRepository.findByAddress(address);
    if (byAddress.isPresent()) {
      throw new DuplicatePlaceException();
    }

    PlaceEntity place = PlaceEntity.builder()
        .memberId(member)
        .placeName(placeRequest.getPlaceName())
        .address(address)
        .x(placeRequest.getX())
        .y(placeRequest.getY())
        .placeInfo(placeRequest.getPlaceInfo())
        .category(placeRequest.getCategory())
        .reportCnt(0)
        .bookmarkCnt(0L)
        .build();
    placeRepository.save(place);

    // 이미지 저장
    for (MultipartFile file : files) {
      String uuid = UUID.randomUUID().toString();
      String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\places";
      String fileName = uuid + "_" + file.getOriginalFilename();
      File saveFile = new File(projectPath, fileName);
      try {
        file.transferTo(saveFile);
      } catch (Exception e) {
        throw new ImageSaveException();
      }

      ImageEntity image = ImageEntity.builder()
          .fileName(fileName)
          .filePath(projectPath + "\\" + fileName)
          .useType(UseType.PLACE)
          .placeId(place)
          .build();
      imageRepository.save(image);
    }
  }

  @Override
  public PlaceDto getInfo(String accessToken, Long placeId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new NotFoundPlaceException());

    Optional<BookmarkPlaceEntity> byMemberIdAndPlaceId = bookmarkPlaceRepository.findByMemberIdAndPlaceId(
        member, place);

    List<ImageEntity> images = imageRepository.findByPlaceId(place);
    List<String> imageUrl = new ArrayList<>();
    for (ImageEntity image : images) {
      imageUrl.add(image.getFilePath());
    }

    PlaceDto result = new PlaceDto();
    if (byMemberIdAndPlaceId.isPresent()) {
      result = PlaceDto.of(place, true, imageUrl);

      return result;
    }

    result = PlaceDto.of(place, false, imageUrl);

    return result;
  }

  @Override
  public List<MapDto> getMapData(String accessToken, Double x, Double y) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    List<PlaceEntity> bySql = placeRepository.findBySql(x, y);
    List<MapDto> result = MapDto.listOf(bySql);

    return result;
  }

  @Override
  public List<PlaceDto> getList(String accessToken, String state, PlaceCategory category) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    List<PlaceEntity> byAddressContaining = placeRepository.findByAddressContainingAndCategory(
        state, category);

    List<List<String>> imageUrl = new ArrayList<>();
    List<Boolean> bookmarkYn = new ArrayList<>();
    for (PlaceEntity place : byAddressContaining) {
      Optional<BookmarkPlaceEntity> byMemberIdAndPlaceId = bookmarkPlaceRepository.findByMemberIdAndPlaceId(
          member, place);
      if (byMemberIdAndPlaceId.isPresent()) {
        bookmarkYn.add(true);
      } else {
        bookmarkYn.add(false);
      }

      List<String> url = new ArrayList<>();
      List<ImageEntity> images = imageRepository.findByPlaceId(place);
      for (ImageEntity image : images) {
        url.add(image.getFilePath());
      }
      imageUrl.add(url);
    }

    List<PlaceDto> result = PlaceDto.listOf(byAddressContaining, bookmarkYn, imageUrl);

    return result;
  }

  @Override
  @Transactional
  public void report(String accessToken, Long placeId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new NotFoundPlaceException());

    PlaceEntity placeEntity = place.toBuilder()
        .reportCnt(place.getReportCnt() + 1)
        .build();
    placeRepository.save(placeEntity);

    // 신고개수가 100개 이상이면 place 삭제
    if (placeEntity.getReportCnt() >= 100) {
      placeRepository.delete(placeEntity);

      // 북마크로 되어있던 것도 삭제
      List<BookmarkPlaceEntity> byPlaceId = bookmarkPlaceRepository.findByPlaceId(placeEntity);
      bookmarkPlaceRepository.deleteAll(byPlaceId);
    }
  }

  @Override
  @Transactional
  public void bookmark(String accessToken, Long placeId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new NotFoundPlaceException());

    // 이미 북마크 한 장소일 경우 북마크 취소되게
    Optional<BookmarkPlaceEntity> byMemberIdAndPlaceId = bookmarkPlaceRepository.findByMemberIdAndPlaceId(
        member, place);
    if (byMemberIdAndPlaceId.isPresent()) {
      BookmarkPlaceEntity bookmarkPlace = byMemberIdAndPlaceId.get();
      bookmarkPlaceRepository.delete(bookmarkPlace);

      PlaceEntity placeEntity = place.toBuilder()
          .bookmarkCnt(place.getBookmarkCnt() - 1)
          .build();
      placeRepository.save(placeEntity);


    } else {
      PlaceEntity placeEntity = place.toBuilder()
          .bookmarkCnt(place.getBookmarkCnt() + 1)
          .build();
      placeRepository.save(placeEntity);

      BookmarkPlaceEntity bookmarkPlace = BookmarkPlaceEntity.builder()
          .memberId(member)
          .placeId(place)
          .build();
      bookmarkPlaceRepository.save(bookmarkPlace);
    }
  }

  @Override
  @Transactional
  public PlaceDto updatePlace(String accessToken, Long placeId, PlaceRequest placeRequest,
      List<MultipartFile> files) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new NotFoundPlaceException());

    String address = getAddress(placeRequest.getX(), placeRequest.getY());

    if (!files.isEmpty() || files != null) {
      // 기존의 이미지를 삭제하고 새로운 이미지로 대체
      List<ImageEntity> images = imageRepository.findByPlaceId(place);
      imageRepository.deleteAll(images);

      for (MultipartFile file : files) {
        String uuid = UUID.randomUUID().toString();
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\places";
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(projectPath, fileName);
        try {
          file.transferTo(saveFile);
        } catch (Exception e) {
          throw new ImageSaveException();
        }

        ImageEntity image = ImageEntity.builder()
            .fileName(fileName)
            .filePath(projectPath + "\\" + fileName)
            .useType(UseType.PLACE)
            .placeId(place)
            .build();
        imageRepository.save(image);
      }
    }

    PlaceEntity updatePlace = place.toBuilder()
        .placeName(placeRequest.getPlaceName())
        .address(address)
        .x(placeRequest.getX())
        .y(placeRequest.getY())
        .placeInfo(placeRequest.getPlaceInfo())
        .category(placeRequest.getCategory())
        .build();
    placeRepository.save(updatePlace);

    List<ImageEntity> images = imageRepository.findByPlaceId(place);
    List<String> imageUrl = new ArrayList<>();
    for (ImageEntity image : images) {
      imageUrl.add(image.getFilePath());
    }

    PlaceDto result = PlaceDto.builder()
        .nickName(updatePlace.getMemberId().getNickname())
        .placeName(updatePlace.getPlaceName())
        .address(updatePlace.getAddress())
        .placeImage(imageUrl)
        .placeInfo(updatePlace.getPlaceInfo())
        .category(updatePlace.getCategory())
        .reportCnt(updatePlace.getReportCnt())
        .bookmarkCnt(updatePlace.getBookmarkCnt())
        .build();

    return result;
  }

  @Value("${spring.keys.naver-client-id}")
  private String clientId;
  @Value("${spring.keys.naver-client-secret}")
  private String clientSecret;

  // 위경도를 받아 해당 지역의 주소(도로명)를 받아오는 메서드
  private String getAddress(Double x, Double y) {
    RestTemplate restTemplate = new RestTemplate();
    String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?" +
        "request=coordsToaddr" +
        "&coords=" + y + "," + x +
        "&sourcecrs=epsg:4326" +
        "&orders=roadaddr" +
        "&output=json" +
        "&orders=legalcode";

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
    headers.set("X-NCP-APIGW-API-KEY", clientSecret);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity,
        String.class);
    String result = mapParser(response.getBody());

    return result;
  }

  // 네이버 지도 API Parser
  private String mapParser(String jsonString) {
    try {
      JsonNode jsonNode = objectMapper.readTree(jsonString);

      if (jsonNode != null && jsonNode.size() > 0) {
        if (jsonNode.path("status").path("code").asInt() == 0) {

          JsonNode resultsNode = jsonNode.path("results").get(0).path("region");
          String area1 = resultsNode.path("area1").path("name").asText();
          String area2 = resultsNode.path("area2").path("name").asText();

          JsonNode resultsNode2 = jsonNode.path("results").get(0).path("land");
          String road1 = resultsNode2.path("name").asText();
          String road2 = resultsNode2.path("number1").asText();
          String road3 = resultsNode2.path("number2").asText();

          StringBuilder result = new StringBuilder();
          result.append(area1).append(" ").append(area2).append(" ").append(road1).append(" ")
              .append(road2).append(" ").append(road3);

          return result.toString();
        } else {
          String errorMessage = jsonNode.path("status").path("message").asText();
          return errorMessage;
        }
      }
    } catch (JsonProcessingException e) {
      throw new ParsingException();
    }
    throw new ParsingException();
  }

}
