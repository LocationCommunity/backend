// src/components/Footer.js
import React from 'react';
import './Footer.css';
import { FaMap } from "react-icons/fa";
import { GoHome } from "react-icons/go";
import { useNavigate } from 'react-router-dom'; 

const Footer = () => {
  const navigate = useNavigate();

  const navigateTo = (path) => {
    navigate(path);
  };

  return (
    <footer className="footer">
      <button onClick={() => navigateTo('/')}><GoHome size={25}/></button>
      <button onClick={() => navigateTo('/map')}><FaMap size={25}/></button>
      <button onClick={() => navigateTo('/about')}>About</button>
    </footer>
  );
};

export default Footer;
