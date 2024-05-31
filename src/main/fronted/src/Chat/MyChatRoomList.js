import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import './MyChatRoomList.css';
import { AuthContext } from '../Context/AuthContext';

function MyChatRoomList() {
    const { memberId } = useContext(AuthContext);
    const [chatRooms, setChatRooms] = useState([]);

    useEffect(() => {
        async function fetchChatRooms() {
            try {
                const token = localStorage.getItem('accessToken');
                const response = await axios.get(`/chat/room/list?memberId=${memberId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                setChatRooms(response.data.data);
            } catch (error) {
                console.error('Error fetching chat rooms:', error);
            }
        }
        fetchChatRooms();
    }, [memberId]);

    return (
        <div className="chat-room-container">
            <h2 className="chat-room-header">채팅방 목록</h2>
            <ul className="chat-room-list">
                {chatRooms.map(room => (
                    <li key={room.id} className="chat-room-item">
                    {/* <Link 
                        to={{
                            pathname: `/chat/room/${room.id}`,
                            state: { roomId: room.id }
                        }} */}< Link to={`/chat/room/${room.id}`} state={{ roomId: room.id }}
                    >
                        {room.matchedMember2.nickname} : 님과의 채팅방
                    </Link>
                </li>
                ))}
            </ul>
        </div>
    );
}

export default MyChatRoomList;
