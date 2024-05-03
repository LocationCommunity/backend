package com.easytrip.backend.chatting.handler;

import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.type.Platform;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompChatHandler implements ChannelInterceptor {



    private final JwtTokenProvider jwtTokenProvider;


    private Map<String, Integer> sessions = new HashMap<>();

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // WebSocket 연결 요청인 경우 JWT 토큰 유효성 검사 수행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 헤더에서 Authorization 토큰 추출
            final String BEARER = "Bearer ";

            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith(BEARER)) {
                token = token.substring(BEARER.length()); // "Bearer " 이후의 토큰 값만 추출
            }

            // JWT 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 유효한 경우 토큰에서 클레임 정보 추출
                Claims claims = jwtTokenProvider.getClaimsFromToken(token);
                // 추출된 클레임 정보를 사용하여 필요한 작업 수행
                String email = claims.getSubject();
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);
                Platform platform = jwtTokenProvider.getPlatform(token);

                log.info("사용자 {}가 접속하셨습니다.", email);
                log.info("유저타입은 {} 입니다.", roles);
                log.info("로그인 경로는 {}입니다.", platform);
            } else {
                log.error("유효하지 않은 토큰입니다.");
                throw new RuntimeException();
            }
        }
        return message;
    }

//
}