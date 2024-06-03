import React, { useState, useEffect, useRef, useContext } from 'react';
import { Client } from '@stomp/stompjs';
import './chatroom.css';
import { AuthContext } from '../Context/AuthContext';

function ChatRoom({ roomId, accessToken, initialMessages }) {

  const {  memberId } = useContext(AuthContext);
  const [messages, setMessages] = useState(initialMessages);
  const [messageInput, setMessageInput] = useState('');
  const stompClientRef = useRef(null);

  useEffect(() => {
    // Stomp 클라이언트 생성
    stompClientRef.current = new Client({
      brokerURL: 'ws://localhost:8080/stomp/chat',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      connectHeaders: {
        Authorization: `Bearer ${accessToken}` // 토큰을 헤더에 포함
      }
    });

    // 연결 설정
    stompClientRef.current.onConnect = () => {
      console.log('Connected to WebSocket');
      // 구독 설정
      stompClientRef.current.subscribe(`/exchange/chat.exchange/room.${roomId}`, (message) => {
        const receivedMessage = JSON.parse(message.body);
        setMessages(prevMessages => Array.isArray(prevMessages) ? [...prevMessages, receivedMessage] : [receivedMessage]);
      });
    };

    // 연결 시작
    stompClientRef.current.activate();

    // 컴포넌트 언마운트 시 연결 해제
    return () => {
      if (stompClientRef.current) stompClientRef.current.deactivate();
    };
  }, [roomId, accessToken]); // 컴포넌트가 처음 마운트될 때와 roomId, accessToken이 변경될 때 실행

  // 메시지 전송 함수
  const sendMessage = () => {
    if (messageInput.trim() === '') {
      return;
    }

    const message = {
      message: messageInput,
      senderId: memberId, // 현재 사용자의 ID
      receiverId: {matchedMember2}, // 수신자 ID (필요에 따라 변경)
      roomId: 20, // 채팅방 ID
      sendTime: new Date().toISOString().split('.')[0] // 현재 시간을 ISO 형식으로 저장
      
    };

    stompClientRef.current.publish({
      destination: `/pub/chat.talk.${roomId}`,
      body: JSON.stringify(message),
      headers: {
        Authorization: `Bearer ${accessToken}` // stomp 헤더에 토큰 추가
      }
    });

    setMessageInput('');  // 메시지 입력 필드 비우기
  };

  return (
    <div className="chat-container">
      {Array.isArray(messages) && messages.map((message, index) => (
        
        <div key={index} className="message">
          <p>
            {/* <span className="sender-id">{memberId}:</span> {message.message} {new Date(message.sendTime).toLocaleString()} */}
          </p>
          <p>
          {/* <span className="receiver-id">{message.receiverId}:</span> {message.message} {new Date(message.sendTime).toLocaleString()} */}
          </p>
        </div>
      ))}
      <div className="input-container">
        <input
          type="text"
          value={messageInput}
          onChange={(e) => setMessageInput(e.target.value)}
          className="input-field"
          placeholder="메시지를 입력하세요..."
        />
        <button onClick={sendMessage} className="send-button">Send</button>
      </div>
    </div>
  );
}

export default ChatRoom;
