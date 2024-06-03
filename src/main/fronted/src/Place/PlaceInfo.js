import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import NaverMap from './NaverMap'; // NaverMap 컴포넌트 임포트
import './PlaceInfo.css'; // CSS 파일 임포트

const PlaceInfo = () => {
  const [placeInfo, setPlaceInfo] = useState(null);
  const { placeId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const accessToken = document.cookie
          .split('; ')
          .find(row => row.startsWith('accessToken='))
          .split('=')[1];

        if (!accessToken) {
          navigate('/members/login');
          return;
        }

        const response = await axios.get(`/place/info/${placeId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}` // 토큰을 헤더에 포함하여 요청 보내기
          }
        });

        setPlaceInfo(response.data);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [placeId, navigate]);

  if (!placeInfo) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <>
    <div className="image-container">
          {placeInfo.placeImage && placeInfo.placeImage.map((image, index) => (
            <img key={index} src={`/images/${image}`} alt={`장소 이미지 ${index + 1}` } />
          ))}
        </div>
      
      <div className="container">
        <h2>{placeInfo.placeName}</h2>
        <p>주소: {placeInfo.address}</p>
        <p>카테고리: {placeInfo.category}</p>
        
      </div>
      {placeInfo.x && placeInfo.y && (
          <div className="map-container">
            <NaverMap x={placeInfo.x} y={placeInfo.y} />
          </div>
        )}
    </>
  );
};

export default PlaceInfo;
