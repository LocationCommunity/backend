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
    public ResponseEntity<BasicResponse> JoinChatRoom(@RequestBody ChatRoomDto.Request join) {

        try {
            Long roomId = chatRoomService.joinChatRoom(join);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Result<>(roomId));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "404"));

        }
    }

    //채팅방 목록
    @GetMapping("/chat/room")
    public ResponseEntity<BasicResponse> getChatRoomList(@RequestParam(value = "id") Long memberId) {
        return ResponseEntity.ok(new Result<>(chatRoomService.getRoomList(memberId)));

    }

    //채팅방 디테일
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<BasicResponse> getChatRoomDetail(@PathVariable(value = "roomId") Long roomId) {

        return ResponseEntity.ok(new Result<>(chatRoomService.getRoomDetail(roomId)));


    }












}
