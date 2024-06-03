//package com.easytrip.backend.chatting.controller;
//
//import com.easytrip.backend.chatting.repository.ChatRoomRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping(value = "/chat")
//@Log4j2
//public class RoomController {
//
//    private final ChatRoomRepository repository;
//
//    //채팅방 목록 조회
//    @GetMapping(value = "/rooms")
//    private ModelAndView rooms() {
//        log.info("# All Chat Rooms");
//        ModelAndView mv = new ModelAndView("chat/rooms");
//
//        mv.addObject("list", repository.findAllRooms());
//
//        return mv;
//    }
//
//    //채팅방 개설
//    @PostMapping(value = "/room")
//    public String create(@RequestParam(value = "name")String name,  RedirectAttributes rttr) {
//        log.info("# Create Chat Room, name : " + name);
//        rttr.addFlashAttribute("roomName", repository.createChatRoomDto(name));
//        return "redirect:/chat/rooms";
//    }
//
//    //채팅방 조회
//    @GetMapping("/room")
//    public void getRoom(@RequestParam(value = "roomId") String roomId,  Model model) {
//        log.info("# get Chat Room, roomId" + roomId);
//
//        model.addAttribute("room", repository.findRoomById(roomId));
//    }
//}
