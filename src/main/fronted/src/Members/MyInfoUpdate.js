import React, { useState, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../Context/AuthContext';

function MyInfoUpdate({ onInfoUpdate }) {
  const [file, setFile] = useState(null);
  const { accessToken } = useContext(AuthContext);
  const [updateRequest, setUpdateRequest] = useState({
    nickname: '',
    introduction: ''
  });
  const [notification, setNotification] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUpdateRequest({
      ...updateRequest,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('nickname', updateRequest.nickname);
    formData.append('introduction', updateRequest.introduction);
    formData.append('file', file);

    try {
      const response = await axios.put('/members/my-info', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${accessToken}`
        }
      });
      console.log('회원 정보가 업데이트되었습니다:', response.data);
      setNotification('회원 정보가 성공적으로 업데이트되었습니다.');
      // 부모 컴포넌트에서 사용자 정보 다시 불러오기
      onInfoUpdate();
    } catch (error) {
      console.error('회원 정보 업데이트에 실패했습니다:', error);
      setNotification('회원 정보 업데이트에 실패했습니다.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="text" 
        name="nickname" 
        value={updateRequest.nickname}
        placeholder="닉네임" 
        onChange={handleInputChange} 
      />
      <input 
        type="text" 
        name="introduction" 
        value={updateRequest.introduction}
        placeholder="소개" 
        onChange={handleInputChange} 
      />
      <input type="file" onChange={handleFileChange} />
      <button type="submit">업데이트</button>
      {notification && <p>{notification}</p>}
    </form>
  );
}

export default MyInfoUpdate;
