// import { useEffect, useState } from "react";
// import axios from "axios";
// import './App.css';  // CSS 파일을 import 합니다.

// function App() {
//     const [boards, setBoards] = useState({});

//     useEffect(() => {
//         axios.get('http://localhost:8080/boards/6')
//         .then((res) => {
//             setBoards(res.data);
//         })
//         .catch((error) => {
//             console.error('Error fetching data:', error);
//         });
//     }, []);

//     return (
//         <div className="App">
//             {boards.title ? (
//                 <div>
//                     <div className="image-container">
//                         {boards.images && boards.images.map((image, index) => (
//                             <img key={index} src={`/boards/images/${image}`} alt={`게시판 이미지 ${index + 1}`} />
//                         ))}
//                     </div>
//                     <div className="content">
//                         <h1>제목 : {boards.title}</h1>
//                         <p>내용 : {boards.content}</p>
//                         <p>주소 : {boards.address}</p>
//                     </div>
//                 </div>
//             ) : (
//                 <p className="loading">데이터를 불러오는 중...</p>
//             )}
//         </div>
//     );
// }

// export default App;

import React from 'react';
import {  Route, Routes } from 'react-router-dom';
import Footer from './Layout/Footer';
import Home from './Home/Home';
import Map from './Place/Map';
import Boards from './Boards/Boards'
import BoardWrite from './Boards/BoardWrite'
import BoardsList from './Boards/BoardsLists';
import Weather from './Weather/Weather';
import LoginPage from './Members/LoginPage';
import MyInfo from './Members/MyInfo';
import MyChatRoomList from './Chat/MyChatRoomList';
import ChatRoomDetail from './Chat/ChatRoomDetail';
import Matching from './Matching/Matching';
import { AuthProvider } from './Context/AuthContext';
import Nav from './Layout/Nav';
import PlaceList from './Place/PlaceList';
import PlaceInfo from './Place/PlaceInfo';
import MyInfoUpdate from './Members/MyInfoUpdate';



const App = () => {
  return (
    <AuthProvider>
        <header><Nav/></header>
      <div className="App">
        <Routes>
        <Route path="/" element={<Home />} />
          <Route path="/map" element={<Map/>} />
          <Route path="/weather/data" element={<Weather />} />
          <Route path="/boards/lists" element={<BoardsList />} />
          <Route path="/boards" element={<BoardWrite />} />
          <Route path="/boards/:boardId" element={<Boards />} /> Route 안에서는 element prop을 사용합니다.
          <Route path="/members/login" element={<LoginPage/>} />
          <Route path="/members/my-info" element={<MyInfo/>} />
          <Route path="/chat/room/list" element={<MyChatRoomList/>} />
          <Route path="/chat/room/:roomId" element={<ChatRoomDetail/>} /> state 로 roomid 값 받아옴.
          <Route path="/matching" element={<Matching/>} />
          <Route path="/place/list" element={<PlaceList />} />
          <Route path="/place/info/:placeId" element={<PlaceInfo/>} />
          <Route path="/members/my-info/update" element={<MyInfoUpdate />} />
          
          
        </Routes>
         <Footer />
      </div>
      </AuthProvider>
    
  );
};

export default App;