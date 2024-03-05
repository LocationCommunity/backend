package com.easytrip.backend.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDto {

  private Double pop;
  private String pty;
  private String  pcp;
  private Double reh;
  private Integer sno;
  private String sky;
  private Integer tmp;
  private Integer tmn;
  private Integer tmx;
  private String fcstDate;
  private String fcstTime;
}
