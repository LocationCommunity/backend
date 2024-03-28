package com.easytrip.backend.common.image.service.impl;

import com.easytrip.backend.common.image.entity.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.common.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;



    public String saveFile(ImageEntity imageEntity, MultipartFile file) throws Exception {


// 단일 저장

        // 저장 경로 설정
        String projectPath = System.getProperty("user.dir") + "\\src.\\main\\resources\\static\\files";
        UUID uuid = UUID.randomUUID();

        // 랜덤식별자_원래이름
        String fileName = uuid + "_" + file.getOriginalFilename();

        // 빈 껍데기 생성
        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        ImageEntity image = ImageEntity.builder()
                .filePath(imageEntity.getFilePath())
                .fileName(saveFile.getName())
                .build();

        imageRepository.save(image);


        return "이미지 저장완료";
    }





}
