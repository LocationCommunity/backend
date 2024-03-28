//package com.easytrip.backend.common.image.controller;
//
//import com.easytrip.backend.common.image.entity.ImageEntity;
//import com.easytrip.backend.common.image.service.ImageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/save")
//public class imageController {
//
//    private final ImageService imageService;
//
//    @PostMapping("/file")
//    public ResponseEntity<String> saveFile(@ModelAttribute ImageEntity imageEntity, @RequestPart("file") MultipartFile file) throws IOException {
//
//        String response = imageService.saveFile(imageEntity, file);
//
//
//        return ResponseEntity.ok(response);
//
//    }
//}
