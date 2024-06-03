import React, { useEffect, useState, useContext } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import './ChatRoomDetail.css';
import { Client } from '@stomp/stompjs';
import { AuthContext } from '../Context/AuthContext';





function ChatRoomDetail() {
  const [roomDetail, setRoomDetail] = useState(null);
  const [newMessage, setNewMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const { memberId } = useContext(AuthContext); 
  const [error, setError] = useState(null);
  const location = useLocation();
  const roomIdFromLocation = location.state ? location.state.roomId : null;

  

  

  useEffect(() => {
    if (!roomIdFromLocation) {
      return;
    }

    const fetchRoomDetail = async () => {
      try {
        const accessToken = localStorage.getItem('accessToken');
        const response = await axios.get(`/chat/room/${roomIdFromLocation}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        });
        setRoomDetail(response.data.data);
        setMessages(response.data.data.messages);
      } catch (error) {
        setError('채팅방 정보를 불러오는데 실패했습니다.');
      }
    };

    fetchRoomDetail();
  }, [roomIdFromLocation]);

  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken');
    const client = new Client({
      brokerURL: 'ws://localhost:8080/stomp/chat',
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`
      },
      onConnect: () => {
        client.subscribe(`/exchange/chat.exchange/room.${roomIdFromLocation}`, message => {   // roomId를 구독하고 있다.그럼 roomId 로의 메시지를 받는다.
          const receivedMessage = JSON.parse(message.body);
          setMessages(prevMessages => [...prevMessages, receivedMessage]);
        });
      },
      onStompError: frame => {
        console.error(`Broker reported error: ${frame.headers['message']}`);
        console.error(`Additional details: ${frame.body}`);
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [roomIdFromLocation]);

  const sendMessage = () => {
    const accessToken = localStorage.getItem('accessToken');
    // const x = Number(memberId);
    const message = {
      message: newMessage,
      senderId:  memberId, // 실제 사용자 ID로 변경 필요 ((토큰 정보의 id))
      receiverId: 2, // 실제 받는 사람 ID로 변경 필요 // 토큰 정보의 id          
      roomId: roomIdFromLocation,
      sendTime: new Date().toISOString()
    };

    // senderId : memberId (토큰)

    // 채팅방 멤버 아이디들은 MatchedMember1, MatchedMember2

    const client = new Client({
      brokerURL: 'ws://localhost:8080/stomp/chat',
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`
      },
      onConnect: () => {
        client.publish({
          destination: `/pub/chat.talk.${roomIdFromLocation}`,
          body: JSON.stringify(message),
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        });
        setNewMessage('');
      },
    });

    client.activate();
  };

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!roomDetail) {
    return <div>Loading...</div>;
  }

  return (
    <div className="chat-room-detail">
      <div className="chat-room-detail__header">
        <h1>Chat Room Detail</h1>
      </div>
      <div className="chat-room-detail__info">
        <p>Room ID: {roomDetail.id}</p>
        <p>Matched Member 1: {roomDetail.matchedMember1.nickname}</p>
        <p>Matched Member 2: {roomDetail.matchedMember2.nickname}</p>
        <p>Matched : {typeof memberId}</p>
      </div>
      <div className="chat-room-detail__messages">
        <h2>Messages</h2>
        <ul>
          {messages.map((message, index) => (
            <li key={index} className="message">
              <p className="message__text">{message.senderId}: {message.message}</p>
              <p className="message__time">Send Time: {message.sendTime}</p>
            </li>
          ))}
        </ul>
      </div>
      <div className="chat-room-detail__input">
        <input
          type="text"
          value={newMessage}
          onChange={e => setNewMessage(e.target.value)}
        />
        <button onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
}

export default ChatRoomDetail;
