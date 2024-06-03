package com.easytrip.backend.chatting.controller;


import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.chatting.service.ChatRoomService;
import com.easytrip.backend.common.image.chat.BasicResponse;
import com.easytrip.backend.common.image.chat.ErrorResponse;
import com.easytrip.backend.common.image.chat.Result;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

public class ChatRoomController {


    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;





    //채팅방 생성, 참여
    @PostMapping("/chat/room")
    public ResponseEntity<BasicResponse> JoinChatRoom(HttpServletRequest request, @RequestBody ChatRoomDto.Request join) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        try {
            Long roomId = chatRoomService.joinChatRoom(accessToken, join);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Result<>(roomId));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "404"));

        }
    }

    //채팅방 목록
    @GetMapping("/chat/room/list")
    public ResponseEntity<BasicResponse> getChatRoomList(HttpServletRequest request, @RequestParam(value = "memberId") Long memberId) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        return ResponseEntity.ok(new Result<>(chatRoomService.getRoomList(accessToken, memberId)));

    }

    //채팅방 디테일
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<BasicResponse> getChatRoomDetail(HttpServletRequest request, @PathVariable(value = "roomId") Long roomId) {

        String accessToken = jwtTokenProvider.resolveToken(request);
        return ResponseEntity.ok(new Result<>(chatRoomService.getRoomDetail(accessToken, roomId)));


    }












}
