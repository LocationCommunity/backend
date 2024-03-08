package com.easytrip.backend.place.service;

import com.easytrip.backend.exception.impl.DuplicatePlaceException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.exception.impl.NotFoundPlaceException;
import com.easytrip.backend.exception.impl.ParsingException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.place.repository.PlaceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
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

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;
  private final ObjectMapper objectMapper;
  private final PlaceRepository placeRepository;

  @Override
  @Transactional
  public String share(String accessToken, PlaceRequest placeRequest) {

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
        .placeImage(placeRequest.getPlaceImage())
        .placeInfo(placeRequest.getPlaceInfo())
        .category(placeRequest.getCategory())
        .reportCnt(0)
        .bookmarkCnt(0L)
        .build();
    placeRepository.save(place);

    return "장소를 공유했습니다.";
  }

  @Override
  public PlaceDto getInfo(String accessToken, Long placeId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(() -> new NotFoundPlaceException());

    PlaceDto result = PlaceDto.of(place);

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
    } catch (JsonProcessingException e) {
      throw new ParsingException();
    }
  }
}
