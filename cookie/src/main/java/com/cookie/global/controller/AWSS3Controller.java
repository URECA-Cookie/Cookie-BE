package com.cookie.global.controller;

import com.cookie.global.service.AWSS3Service;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AWSS3Controller {
    private final AWSS3Service awsS3Service;

    @Hidden
    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value="image", required=false) MultipartFile image) {
        String fileName = awsS3Service.uploadImage(image);
        return ResponseEntity.ok(fileName);
    }

}
