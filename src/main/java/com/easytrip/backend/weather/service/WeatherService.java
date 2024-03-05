package com.easytrip.backend.weather.service;

import com.easytrip.backend.weather.dto.WeatherDto;
import org.springframework.stereotype.Service;

@Service
public interface WeatherService {

  WeatherDto getData(Double x, Double y);

  WeatherDto getData(String address);
}
