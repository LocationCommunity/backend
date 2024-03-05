package com.easytrip.backend.weather.service;

import com.easytrip.backend.exception.impl.ParsingException;
import com.easytrip.backend.weather.dto.WeatherDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

  private final ObjectMapper objectMapper;
  private final GpsTransfer gpsTransfer;
  private final RedisTemplate<String, WeatherDto> redisWeatherTemplate;

  @Value("${spring.keys.api-key}")
  private String key;

  @Override
  public WeatherDto getData(Double x, Double y) {

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String currentDate = now.format(dateFormatter);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");
    String currentTime = now.format(timeFormatter);

    // Redis에서 현재 좌표에 해당하는 날씨정보가 있는지 확인, 있다면 그 정보를 제공
    // 좌표를 주소로 변경
    String address = getAddress(x, y);
    WeatherDto weatherDto = redisWeatherTemplate.opsForValue().get("Address: " + address);
    if (weatherDto != null) {
      return weatherDto;
    }

    // 위경도를 격자 x, y 좌표로 변환
    GpsTransfer.LatXLngY latXLngY = gpsTransfer.convertGRID_GPS(0, x, y);

    // 격자 x, y 좌표를 이용해서 단기예보 API를 통해 그 좌표의 현재 날씨를 받아옴
    RestTemplate restTemplate = new RestTemplate();
    String apiUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?" +
        "serviceKey=" + key +
        "&pageNo=1&numOfRows=1000&dataType=JSON" +
        "&base_date=" + currentDate +
        "&base_time=0200" +
        "&nx=" + Integer.parseInt(String.valueOf(Math.round(latXLngY.x))) +
        "&ny=" + Integer.parseInt(String.valueOf(Math.round(latXLngY.y)));
    JsonNode itemsArray = null;
    try {
      String jsonString = restTemplate.getForObject(apiUrl, String.class);
      itemsArray  = jsonParser(jsonString);
    } catch (Exception e) {
      throw new ParsingException();
    }

    // 예보 일자와 예보 시간이 현재와 일치하는 데이터만 골라오기
    WeatherDto weather = new WeatherDto();
    for (JsonNode item : itemsArray) {
      String fcstDate = item.path("fcstDate").asText();
      String fcstTime = item.path("fcstTime").asText();

      if (fcstDate.equals(currentDate)) {
        weather.setFcstDate(fcstDate);

        String category = item.path("category").asText();
        if (category.equals("TMX")) {
          weather.setTmx(item.path("fcstValue").asInt());
        } else if (category.equals("TMN")) {
          weather.setTmn(item.path("fcstValue").asInt());
        }

        if (fcstTime.equals(currentTime)) {
          weather.setFcstTime(fcstTime);

          switch (category) {
            case "TMP":
              weather.setTmp(item.path("fcstValue").asInt());
              break;
            case "SKY":
              int sky = item.path("fcstValue").asInt();
              if (sky == 1) {
                weather.setSky("맑음");
              } else if (sky == 3) {
                weather.setSky("구름많음");
              } else {
                weather.setSky("흐림");
              }
              break;
            case "PTY":
              int pty = item.path("fcstValue").asInt();
              if (pty == 0) {
                weather.setPty("없음");
              } else if (pty == 1) {
                weather.setPty("비");
              } else if (pty == 2) {
                weather.setPty("비/눈");
              } else if (pty == 3) {
                weather.setPty("눈");
              } else {
                weather.setPty("소나기");
              }
              break;
            case "POP":
              weather.setPop(item.path("fcstValue").asDouble());
              break;
            case "PCP":
              weather.setPcp(item.path("fcstValue").asText());
              break;
            case "REH":
              weather.setReh(item.path("fcstValue").asDouble());
              break;
            case "SNO":
              weather.setSno(item.path("fcstValue").asInt());
              break;
            case "TMN":
              weather.setTmn(item.path("fcstValue").asInt());
              break;
          }
        }
      }
    }

    // Redis에 저장
    LocalTime nextHour = LocalTime.of(now.getHour() + 1, 0);
    long secondsUntilNextHour = now.until(LocalDateTime.of(now.toLocalDate(), nextHour), ChronoUnit.SECONDS);
    redisWeatherTemplate.opsForValue().set("Address: " + address, weather, secondsUntilNextHour, TimeUnit.SECONDS);

    return weather;
  }

  @Value("${spring.keys.naver-client-id}")
  private String clientId;
  @Value("${spring.keys.naver-client-secret}")
  private String clientSecret;

  // 위경도를 받아 해당 지역의 주소를 받아오는 메서드
  private String getAddress(Double x, Double y) {
    RestTemplate restTemplate = new RestTemplate();
    String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?" +
        "request=coordsToaddr" +
        "&coords=" + y + "," + x +
        "&sourcecrs=epsg:4326" +
        "&output=json" +
        "&orders=legalcode";

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
    headers.set("X-NCP-APIGW-API-KEY", clientSecret);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
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
        String area3 = resultsNode.path("area3").path("name").asText();

        StringBuilder result = new StringBuilder();
        result.append(area1).append(" ").append(area2).append(" ").append(area3);

        return result.toString();
      } else {
        String errorMessage = jsonNode.path("status").path("message").asText();
        return errorMessage;
      }
    } catch (JsonProcessingException e) {
      throw new ParsingException();
    }
  }

  // 단기예보 API Parser
  public JsonNode jsonParser(String jsonString) throws JsonProcessingException {

    JsonNode jsonNode = objectMapper.readTree(jsonString);
    JsonNode itemsArray = jsonNode
        .path("response")
        .path("body")
        .path("items")
        .path("item");

    return itemsArray;
  }
}
