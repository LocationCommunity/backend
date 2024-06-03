import React, { useContext } from 'react';
import './Home.css'; 
import { AuthContext } from '../Context/AuthContext';

const Home = () => {
    const { isLoggedIn, nickname } = useContext(AuthContext);
    return (
       
        
        
        <div className="home-wrapper">

            <div className="home">
                <div className="home-content">
                    {isLoggedIn ? (
                        <h1> 안녕하세요! <span className="highlight-email">{nickname}</span> 님,</h1>
                    ) : (
                        <h1>안녕하세요,</h1>
                    )}
                    <h2>이지트립에 방문하신걸 환영합니다!</h2>
            
                    <p>소중한 경험을 했던 장소들을 공유해보세요!</p>
                </div>
            </div>
        </div>
    );
}

export default Home;