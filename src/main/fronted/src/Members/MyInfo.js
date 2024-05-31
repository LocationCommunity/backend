import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './MyInfo.css';
import MyInfoUpdate from './MyInfoUpdate';

const MyInfo = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate(); // useNavigate 훅 사용

  useEffect(() => {
    const fetchData = async () => {
      try {
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) throw new Error('No access token found');

        const response = await axios.get('/members/my-info', {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        });
        setUserInfo(response.data);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleInfoUpdate = async () => {
    // 사용자 정보 업데이트 후, 다시 사용자 정보를 불러오는 로직
    setLoading(true);
    try {
      const accessToken = localStorage.getItem('accessToken');
      if (!accessToken) throw new Error('No access token found');

      const response = await axios.get('/members/my-info', {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      });
      setUserInfo(response.data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">Error: {error.message}</div>;

  return (
    <div className="my-info-container">
      <h1> 내 정보 </h1>
      <MyInfoUpdate onInfoUpdate={handleInfoUpdate} />
      {userInfo ? (
        <div>
           {userInfo.imageUrl && (
            <div className="image-container">
              <img src={`/images/${userInfo.imageUrl}?${Date.now()}`} alt="프로필 이미지" />
            </div>
          )}
          <p><strong>Name:</strong> {userInfo.name}</p>
          <p><strong>Email:</strong> {userInfo.email}</p>
          <p><strong>introduction:</strong> {userInfo.introduction}</p>
         
          {/* 추가적인 사용자 정보가 있다면 여기에 표시하세요 */}
        </div>
      ) : (
        <p>No user information available.</p>
      )}
      <button className="back-button" onClick={() => navigate(-1)}>
        뒤로가기
      </button>
    </div>
  );
};

export default MyInfo;
