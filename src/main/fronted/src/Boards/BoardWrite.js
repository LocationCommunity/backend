

import React, { useState, useEffect } from "react";
import axios from "axios";
import './BoardWrite.css';

function BoardWrite() {
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    files: [],
    placeId: "",
  });

  const [places, setPlaces] = useState([]);

  // 쿠키에서 accessToken 가져오는 함수
  const getAccessTokenFromCookie = () => {
    const cookies = document.cookie.split(';');
    const cookie = cookies.find(cookie => cookie.trim().startsWith('accessToken='));
    if (cookie) {
      return cookie.split('=')[1];
    } else {
      return null;
    }
  };

  const accessToken = getAccessTokenFromCookie(); // 쿠키에서 accessToken 가져오기
  console.log(accessToken)
  useEffect(() => {
    async function fetchPlaces() {
      try {
        const response = await axios.get("/place/list", {
          params: {
            state: "부천",
            PlaceCategory: "RESTAURANT",
          },
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        console.log("장소 목록:", response.data);
        setPlaces(response.data);
      } catch (error) {
        console.error("장소 목록을 불러오는 중 에러 발생:", error);
      }
    }

    fetchPlaces();
  }, [accessToken]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleFileChange = (e) => {
    setFormData({
      ...formData,
      files: e.target.files,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const config = {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${accessToken}`,
        },
      };

      const formDataToSend = new FormData();
      formDataToSend.append("title", formData.title);
      formDataToSend.append("content", formData.content);
      formDataToSend.append("placeId", formData.placeId);
      for (let i = 0; i < formData.files.length; i++) {
        formDataToSend.append("files", formData.files[i]);
      }

      await axios.post("/boards/write", formDataToSend, config);
      alert("게시물이 성공적으로 작성되었습니다.");
    } catch (error) {
      console.error("게시물 작성 에러:", error);
    }
  };

  return (
    <div>
      <h2>게시물 작성</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="title">제목:</label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="content">내용:</label>
          <textarea
            id="content"
            name="content"
            value={formData.content}
            onChange={handleChange}
            required
          ></textarea>
        </div>
        <div>
          <label htmlFor="files">파일 첨부:</label>
          <input
            type="file"
            id="files"
            name="files"
            multiple
            onChange={handleFileChange}
          />
        </div>
        <div>
          <label htmlFor="placeId">장소 선택:</label>
          <select
            id="placeId"
            name="placeId"
            value={formData.placeId || ''}
            onChange={handleChange}
            required
          >
            <option key="" value="">장소를 선택하세요</option>
            {places.map((place) => (
              <option key={place.placeId} value={place.placeId}>
                {place.placeName}
              </option>
            ))}
          </select>
        </div>
        <button type="submit">작성 완료</button>
      </form>
    </div>
  );
}

export default BoardWrite;



