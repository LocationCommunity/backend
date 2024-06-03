
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Matching.css';
import { FaHandHoldingHeart } from "react-icons/fa6";

const MatchingList = () => {
  const [matchingList, setMatchingList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) throw new Error('No access token found');

        const response = await axios.get('/matching', {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        });
        setMatchingList(response.data);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleAccept = async (memberId) => {
    try {
      const accessToken = localStorage.getItem('accessToken');
      if (!accessToken) throw new Error('No access token found');

      await axios.post(
        '/matching',
        null, // no body content
        {
          headers: {
            Authorization: `Bearer ${accessToken}`
          },
          params: {
            memberId: memberId
          }
        }
      );
      
      // 수락 후 매칭 리스트를 다시 로드
      const response = await axios.get('/matching', {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      });
      setMatchingList(response.data);
      
    } catch (err) {
      setError(err);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">Error: {error.message}</div>;

  return (
    <div className="matching-list-container">
      <h2> 매칭 리스트 </h2>
      {matchingList.length > 0 ? (
        <ul>
          {matchingList.map((member) => (
            <li key={member.id}>
              <p><strong></strong> {member.nickname}</p>
              
              {/* 추가적인 사용자 정보가 있다면 여기에 표시하세요 */}
              <button
                className="accept-button"
                onClick={() => handleAccept(member.memberId)}
              >
                <FaHandHoldingHeart />
              </button>
            </li>
          ))}
        </ul>
      ) : (
        <p>No matching members found.</p>
      )}
      <button className="back-button" onClick={() => navigate(-1)}>
        뒤로가기
      </button>
    </div>
  );
};

export default MatchingList;
