package com.cookie.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AWSS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String uploadImage(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
        String s3Filename = UUID.randomUUID().toString().substring(0, 10) + originalFilename;

        InputStream is = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            is = image.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType("image/" + extention);
            metaData.setContentLength(bytes.length);

            byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectRequest putObjectRequest
                    = new PutObjectRequest(bucketName, s3Filename, byteArrayInputStream, metaData)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try {
                byteArrayInputStream.close();
                is.close();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        return amazonS3.getUrl(bucketName, s3Filename).toString();
    }

    // TO DO - aws delete image 필요
}
