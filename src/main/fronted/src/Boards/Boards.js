import React, { useEffect, useState } from "react";
import axios from "axios";
import {  useParams, useNavigate } from "react-router-dom";
import { FaThumbsUp } from "react-icons/fa";
import './Boards.css';


function Boards() {
    const [boards, setBoards] = useState({});
    const { boardId } = useParams();
    const navigate = useNavigate();
    const [isLiked, setIsLiked] = useState(false);

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

    useEffect(() => {
        const accessToken = getAccessTokenFromCookie(); // 쿠키에서 accessToken 가져오기

        // axios 인터셉터를 사용하여 응답을 가로챔
        axios.interceptors.response.use(response => {
            return response;
        }, error => {
            // 토큰 검증 실패 시
            if (error.response.status === 403) {

                console.log("토큰 검증에 실패했습니다. 로그인이 필요합니다.");
                // 로그인 알람을 띄움
                alert("로그인이 필요합니다.");
                navigate('/members/login');
                
            }
            return Promise.reject(error);
        });

        // 초기화 시 헤더에 인증 토큰 추가
        axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

    }, [navigate]);

    useEffect(() => {
        axios.get(`/boards/${boardId}`)
            .then((res) => {
                setBoards(res.data);
                console.log('Response data:', res.data);
            })
            .catch((error) => {
                console.error('Error fetching data:', error);
            });
    }, [boardId]);

    const handleBackClick = () => {
        navigate('/boards/lists');
    };

    const handleLikeClick = () => {
        setIsLiked(!isLiked);

        axios.post(`/boards/${boardId}/like`)
            .then((res) => {
                setBoards(prevBoards => ({
                    ...prevBoards,
                    likeCnt: isLiked ? prevBoards.likeCnt - 1 : prevBoards.likeCnt + 1
                    
                }));
            })
            .catch((error) => {
                console.error('Error updating like status:', error);
            });
    };

    const handlePlaceInfoClick = () => {
        navigate({
            pathname: `/place/info/${boards.placeId}`,
        });
    };

    return (
        
            <div className="App">
                <div className="image-container">
                    {boards.images && boards.images.map((image, index) => (
                        <img key={index} src={`/images/${image}`} alt={`게시판 이미지 ${index + 1}`} />
                    ))}
                </div>

                {boards.title ? (
                    <div>
                        <div className="content">
                            <h1>제목 : {boards.title}</h1>
                            <p>내용 : {boards.content}</p>
                            <p>주소 : {boards.address}</p>
                            <p>
                                좋아요 : {boards.likeCnt}
                                <button onClick={handleLikeClick}>
                                    <FaThumbsUp />
                                </button>
                            </p>
                            <p>작성자 : {boards.nickname}</p>
                            <p>
                                장소 : <button onClick={handlePlaceInfoClick}>장소상세보기</button>
                            </p>
                        </div>
                        <button onClick={handleBackClick}> 게시물 목록 </button>
                    </div>
                ) : (
                    <p className="loading"> 게시물 불러오는 중... </p>
                )}
            </div>
        
    );
}

export default Boards;

