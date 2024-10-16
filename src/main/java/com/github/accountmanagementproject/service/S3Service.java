package com.github.accountmanagementproject.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;

    /*
    upload 메소드 사용법 :
    1. service단에서 MultipartFile과 저장할 폴더 이름(ex. blog_image, chat_image 등) 을 매개변수로 넘겨준다.
    * */
    public String upload(MultipartFile file, String dirName) throws IOException {
        //파일명 생성 : (랜덤 UUID_원본파일이름)
        String fileName = UUID.randomUUID() +"_"+file.getOriginalFilename();
        //S3 저장경로 생성 : (dirName/파일명)
        String filePath = dirName+ "/" +fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        s3Client.close();

        return String.format("https://%s.s3.%s.amazonaws.com/%s",bucket, region, filePath);
    }
}
