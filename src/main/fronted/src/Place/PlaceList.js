import React, { useState, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../Context/AuthContext';
import './PlaceList.css';


const categories = [
  { label: '카페', value: 'CAFE' },
  { label: '음식점', value: 'RESTAURANT' },
  { label: '술집', value: 'BAR' },
  { label: '박물관/전시관', value: 'MUSEUM' },
  { label: '쇼핑', value: 'SHOPPING' },
  { label: '공원', value: 'PARK' },
  { label: '엔터테인먼트', value: 'ENTERTAINMENT' }
];

function PlaceList() {
  const [places, setPlaces] = useState([]);
  const [state, setState] = useState('');
  const [category, setCategory] = useState('');
  const [loading, setLoading] = useState(false);
  const [hoveredIndex, setHoveredIndex] = useState(null); // 호버된 이미지의 인덱스를 추적
  const [searched, setSearched] = useState(false);

  const { accessToken } = useContext(AuthContext);

  const fetchData = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`/place/list?state=${state}&PlaceCategory=${category}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      });
      setPlaces(response.data);
      setSearched(true);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
    setLoading(false);
  };

  const handleSearch = (event) => {
    event.preventDefault();
    fetchData();
  };

  const handleCategoryChange = (event) => {
    setCategory(event.target.value);
  };

  const handleImageHover = (index) => {
    setHoveredIndex(index);
  };

  const handleImageHoverEnd = () => {
    setHoveredIndex(null);
  };

  return (
    <div className="place-list-container">
      <h1>장소 목록</h1>
      <form onSubmit={handleSearch}>
        <label htmlFor="state">상태:</label>
        <input
          type="text"
          id="state"
          value={state}
          onChange={(e) => setState(e.target.value)}
        />
        <label htmlFor="category">카테고리:</label>
        <select
          id="category"
          value={category}
          onChange={handleCategoryChange}
          className="custom-select"
        >
          <option value="">선택해주세요</option>
          {categories.map((category, index) => (
            <option key={index} value={category.value} className="custom-option">
              {category.label}
            </option>
          ))}
        </select>
        <button type="submit">검색</button>
      </form>
      {loading && <p>검색 중..</p>}
      {searched && places.length ===  0 && !loading && <p>검색 결과가 없습니다.</p>}
      {places.length > 0 && (
        <ul className="place-list">
          {places.map((place, index) => (
            <li key={index}>
              <a href={`/place/info/${place.placeId}`}>{place.placeName}</a> 저장수: {place.bookmarkCnt}
              <ul className="place-image-list">
                {place.placeImage.map((image, imageIndex) => (
                  <li
                    key={imageIndex}
                    onTouchStart={() => handleImageHover(imageIndex)}  // onMouseEnter OnMouseLeave
                    onTouchEnd={handleImageHoverEnd}
                    className={hoveredIndex === imageIndex ? 'hovered' : ''}
                  >
                    <img src={`/images/${image}`} alt={`장소 이미지 ${index + 1}`} />
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default PlaceList;
