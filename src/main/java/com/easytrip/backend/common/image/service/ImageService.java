package com.easytrip.backend.common.image.service;


import com.easytrip.backend.common.image.entity.ImageEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ImageService {

    String saveFile(ImageEntity imageEntity, MultipartFile file) throws Exception;


}
