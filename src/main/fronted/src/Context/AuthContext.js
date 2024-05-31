import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';
import { jwtDecode } from "jwt-decode"; // JWT 디코딩 라이브러리 추가

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userId, setUserId] = useState('');
    const [email, setEmail] = useState(''); // 이메일 상태 추가
    const [nickname, setNickname] = useState('');
    const [memberId, setMemberId] = useState('');
    const [accessToken, setAccessToken] = useState('');


    
    useEffect(() => {
        const accessToken = localStorage.getItem('accessToken');
        setIsLoggedIn(!!accessToken);
        const userIdFromStorage = localStorage.getItem('platform');
        setUserId(userIdFromStorage || '');
        setAccessToken(accessToken);

        // 토큰 디코딩 및 이메일 추출
        if (accessToken) {
            const decodedToken = jwtDecode(accessToken);
            const userEmail = decodedToken.sub; // (subject) 클레임에서 이메일 추출
            const memberId = decodedToken.memberId; // 토큰에서 memberId 추출
        const nickname = decodedToken.nickname; // 토큰에서 nickname 추출
        
            setMemberId(memberId); // 상태에 memberId 설정
            setNickname(nickname); // 상태에 nickname 설정
            setEmail(userEmail);
        }
    }, []);

    const login = () => {
        setIsLoggedIn(true);
        localStorage.setItem('platform', 'exampleUserId');
    };

    const logout = async () => {
        try {
            const accessToken = localStorage.getItem('accessToken');
            if (!accessToken) {
                console.error('No token found.');
                return;
            }
            await axios.post("/members/logout", {},{
            
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
                
            });
            localStorage.removeItem('accessToken')
            setIsLoggedIn(false);
            console.log('Logout successful.');
        } catch (error) {
            console.error('Logout request error:', error);
        }
    };

    return (
        <AuthContext.Provider value={{ isLoggedIn, login, logout, userId, email, memberId, nickname, accessToken }}>
            {children}
        </AuthContext.Provider>
    );
};
