
import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import {  Link } from "react-router-dom";
import "./BoardsLists.css"; // CSS 파일 import
import LoadingSpinner from './LoadingSpinner'; 

function BoardsList() {
    const [boardsList, setBoardsList] = useState([]);
    const [page, setPage] = useState(0); // 현재 페이지
    const [isLoading, setIsLoading] = useState(false); // 데이터 로딩 상태
    const pageSize = 10; // 고정된 페이지 크기

    // Intersection Observer 초기화
    const observer = useRef();
    const lastBoardRef = useRef();

    useEffect(() => {
        // Intersection Observer 콜백 함수
        const handleObserver = (entries) => {
            const target = entries[0];
            if (target.isIntersecting && !isLoading) {
                // 페이지 끝에 도달했을 때 다음 페이지 불러오기
                setPage((prevPage) => prevPage + 1);
            }
        };

        // Intersection Observer 생성
        observer.current = new IntersectionObserver(handleObserver, {
            root: null,
            rootMargin: "20px",
            threshold: 0.3,
        });

        const currentRef = lastBoardRef.current;
        if (currentRef) {
            observer.current.observe(currentRef);
        }

        // 컴포넌트 언마운트 시 Observer 해제
        return () => {
            if (currentRef) {
                observer.current.unobserve(currentRef);
            }
        };
    }, [isLoading]); // 빈 의존성 배열로 설정하여 한 번만 실행되도록 함

    useEffect(() => {
        if (isLoading) {
            axios.get(`/boards/lists?page=${page}&size=${pageSize}`)
                .then((res) => {
                    setBoardsList((prevList) => [...prevList, ...res.data]);
                    console.log(res.data);
                })
                .catch((error) => {
                    console.error('Error fetching data:', error);
                })
                .finally(() => {
                    // 데이터 요청이 완료되면 isLoading 상태 변경
                    setIsLoading(false);
                });
        }
    }, [page, isLoading, pageSize]); // isLoading 상태를 의존성 배열에 추가

    // page 상태 변경 시 데이터 로드 시작
    useEffect(() => {
        setIsLoading(true);
    }, [page]);

    useEffect(() => {
        if (lastBoardRef.current && observer.current) {
            observer.current.observe(lastBoardRef.current);
        }
    }, [boardsList]); // boardsList 변경 시 마지막 요소를 다시 관찰

    return (
        
            
                
            
            <div className="boards-container">
                <h1>게시판 목록</h1>
                <Link to="/boards" className="create-post-button">게시물 작성</Link>
                <ul className="boards-list">
                    {boardsList.map((board, index) => (
                        <li className="boards-item" key={index} ref={index === boardsList.length - 1 ? lastBoardRef : null}>
                            <Link className="boards-link" to={`/boards/${board.boardId}`}>
                                <div className="image-container">
                                    {board.boardImage && board.boardImage.map((image, imgIndex) => (
                                        <img key={imgIndex} src={`/images/${image}`} alt={`게시판 이미지 ${imgIndex + 1}`} />
                                    ))}
                                </div>
                                <div className="boards-content">
                                    <h2 className="boards-title">{board.title}</h2>
                                </div>
                            </Link>
                        </li>
                    ))}
                </ul>
                {isLoading && <LoadingSpinner />}
            </div>
        
    );
}

export default BoardsList;

