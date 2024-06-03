import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom'; // useHistory 대신 useNavigate 사용
import axios from 'axios';

const NaverCallback = () => {
  const location = useLocation();
  const navigate = useNavigate(); // useNavigate 사용

  useEffect(() => {
    const code = new URLSearchParams(location.search).get('code');
    if (code) {
      axios.get(`/members/login/naver?code=${code}`)
        .then(response => {
          const { accessToken } = response.data;
          localStorage.setItem('accessToken', accessToken);
          navigate('/'); // navigate 함수를 사용하여 홈 화면으로 이동
        })
        .catch(error => {
          console.error('네이버 로그인 실패:', error);
        });
    }
  }, [location, navigate]); // navigate 추가

  return <div>네이버 로그인 중...</div>;
};

export default NaverCallback;
