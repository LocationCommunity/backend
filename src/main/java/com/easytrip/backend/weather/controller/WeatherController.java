package com.easytrip.backend.weather.controller;

import com.easytrip.backend.weather.dto.WeatherDto;
import com.easytrip.backend.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

  private final WeatherService weatherService;

  @GetMapping("/data")
  public ResponseEntity<WeatherDto> getData(@RequestParam Double x, @RequestParam Double y) {
    WeatherDto response = weatherService.getData(x, y);
    return ResponseEntity.ok(response);
  }
}
