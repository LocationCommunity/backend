import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './LoginPage.css'; // CSS 파일 임포트
import logo from '../Layout/logo.png';
import { IoIosArrowBack } from "react-icons/io";




const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isEmailValid, setEmailValid] = useState(false);
  const [isPasswordValid, setPasswordValid] = useState(false);

  const checkEmailValidity = (email) => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    setEmailValid(emailRegex.test(email));
  };

  const checkPasswordValidity = (password) => {
    setPasswordValid(password.length >= 1);
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!isEmailValid || !isPasswordValid) {
      setError('이메일과 비밀번호를 올바르게 입력하세요.');
      return;
    }
    try {
      const response = await axios.post('/members/login', { email, password });
      console.log('로그인 성공:', response.data);
      const accessToken = getCookie('accessToken');
      if (accessToken) {
        localStorage.setItem('accessToken', accessToken);
      
        window.location.href = '/';
      } else {
        setError('로그인에 실패했습니다. 토큰을 찾을 수 없습니다.');
      }
    } catch (error) {
      console.error('로그인 실패:', error.response ? error.response.data : error);
      setError('로그인에 실패했습니다. 사용자명 또는 비밀번호를 확인하세요.');
    }
  };

  const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
  };

  useEffect(() => {
    const clientId = '_HS3A2uGvmapJz7j8E1s';
    const state = 'STATE_STRING';
    const redirectUri = 'http://localhost:8080/members/login/naver';
    const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${clientId}&state=${state}&redirect_uri=${redirectUri}`;
    
    const naverLoginButton = document.createElement('a');
    naverLoginButton.href = naverAuthUrl;
    naverLoginButton.innerHTML = '<img src="https://static.nid.naver.com/oauth/small_g_in.PNG" alt="네이버 로그인" style="width: 150px; height: auto;" />';
    document.getElementById('naverIdLogin').appendChild(naverLoginButton);
  }, []);

  return (
    <><div>
      <button onClick={() => navigate("/")} className="back-button">
        <IoIosArrowBack size={10} />
      </button>
    </div><div className="login-page">
        <img src={logo} alt="easytrip" className="logo" />
        {error && <p className="error-message">{error}</p>}
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="email">이메일 주소</label>
            <input
              type="email"
              placeholder='예) example@email.com'
              id="email"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                checkEmailValidity(e.target.value);
              } } />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                checkPasswordValidity(e.target.value);
              } } />
          </div>
          <button type="submit" className={isEmailValid && isPasswordValid ? 'active' : ''} disabled={!isEmailValid || !isPasswordValid}>로그인</button>
        </form>
        <div id="naverIdLogin" />
      </div></>
  );
};

export default LoginPage;

