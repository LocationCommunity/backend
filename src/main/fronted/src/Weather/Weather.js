import React, { useEffect, useState } from "react";
import axios from "axios";
import './Weather.css';


function Weather() {
    const [weatherData, setWeatherData] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const fetchWeatherData = async (latitude, longitude) => {
            try {
                const response = await axios.get('/weather/data', {
                    params: {
                        x: latitude,
                        y: longitude
                    }
                });
                const data = response.data;
                data.fcstTime = `${data.fcstTime.slice(0, 2)}:${data.fcstTime.slice(2)}`;
                setWeatherData(data);
                console.log(latitude, longitude);
                console.log(data);
            } catch (error) {
                console.error('Error fetching weather data:', error);
                setErrorMessage('날씨 정보를 불러오는 중 오류가 발생했습니다.');
            }
        };

        const fetchGeolocation = () => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const { latitude, longitude } = position.coords;
                        fetchWeatherData(latitude, longitude);
                    },
                    (error) => {
                        console.error('Error fetching geolocation:', error);
                        setErrorMessage('사용자가 위치 정보를 제공하지 않았습니다.');
                    }
                );
            } else {
                console.error('Geolocation is not supported by this browser.');
                setErrorMessage('이 브라우저는 위치 정보를 지원하지 않습니다.');
            }
        };

        fetchGeolocation();
    }, []);

    return (
        <><div className="App2">
            <div className="Weather">
                <h1>근처 현재날씨</h1>
                <h3>* 날씨는 1시간마다 업데이트 됩니다. </h3>
                {errorMessage ? (
                    <p>{errorMessage}</p>
                ) : weatherData ? (
                    <div className="weather-info">
                        <p className="pop"><i className="fas fa-umbrella"></i> 강수확률: {weatherData.pop}%</p>
                        <p className="pty"><i className="fas fa-cloud-rain"></i> 강수 형태: {weatherData.pty}</p>
                        <p className="pcp"><i className="fas fa-tint"></i> 강수량: {weatherData.pcp}mm</p>
                        <p className="reh"><i className="fas fa-water"></i> 습도: {weatherData.reh}%</p>
                        <p className="sky"><i className="fas fa-cloud"></i> 하늘 상태: {weatherData.sky}</p>
                        <p className="tmp"><i className="fas fa-thermometer-half"></i> 기온: {weatherData.tmp}°C</p>
                        <p className="tmn"><i className="fas fa-temperature-low"></i> 최저 기온: {weatherData.tmn}°C</p>
                        <p className="tmx"><i className="fas fa-temperature-high"></i> 최고 기온: {weatherData.tmx}°C</p>
                        <p className="fcstDate"><i className="fas fa-calendar-day"></i> 예보 날짜: {weatherData.fcstDate}</p>
                        <p className="fcstTime"><i className="fas fa-clock"></i> 예보 시간: {weatherData.fcstTime}</p>
                    </div>
                ) : (
                    <p>날씨 정보를 불러오는 중...</p>
                )}
            </div>
        </div></>
    );
}

export default Weather;