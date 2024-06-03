import React, { useEffect, useRef } from 'react';

const NaverMap = ({ x, y }) => {
  const mapElement = useRef(null);

  useEffect(() => {
    const { naver } = window; // window 객체에서 naver 가져오기
    if (!mapElement.current || !naver) return;

    const mapOptions = {
      center: new naver.maps.LatLng(x, y),
      zoom: 17,
    };

    const map = new naver.maps.Map(mapElement.current, mapOptions);

    new naver.maps.Marker({
      position: new naver.maps.LatLng(x, y),
      map: map,
    });
  }, [x, y]);

  return (
    <div
      ref={mapElement}
      style={{ width: '100%', height: '100%' }} // 적절한 크기로 설정
    />
  );
};

export default NaverMap;
