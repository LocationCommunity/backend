package com.easytrip.backend.configuration;

import com.easytrip.backend.chatting.handler.StompChatHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Log4j2
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private final StompChatHandler stompHandler;
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // stompHandler를 인터셉터로 등록하여 STOMP 메시지 핸들링을 수행
        registration.interceptors(stompHandler);
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {

        //example는 WebSocket 또는 SockJS Client가 웹소켓 핸드셰이크 커넥션을 생성할 경로
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("*");

    }

    public void configureMessageBroker(MessageBrokerRegistry config) {


        // 클라이언트가 "/pub"으로 시작하는 대상에 메시지를 전송합니다.
        config.setApplicationDestinationPrefixes("/pub");

        // SimpleBroker는 해당하는 경로를 SUBSCRIBE하는 Client에게 메세지를 전달하는 간단한 작업을 수행
        // "/sub"으로 시작하는 메시지가 메시지 브로커로 라우팅됩니다.
//

        //enableStompBrokerRelay
        //SimpleBroker의 기능과 외부 Message Broker( RabbitMQ, ActiveMQ 등 )에 메세지를 전달하는 기능을 가짐
//
//        config.enableSimpleBroker("/sub");
        config.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue");







    }

    // STOMP에서 64KB 이상의 데이터 전송을 못하는 문제 해결
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(160 * 64 * 1024);
        registry.setSendTimeLimit(100 * 10000);
        registry.setSendBufferSizeLimit(3 * 512 * 1024);
    }
}