package com.github.accountmanagementproject.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.github.accountmanagementproject.service.customExceptions.StorageUpdateFailedException;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;


    //파일 업로드 및 원본파일명, URL 반환
    public List<FileDto> fileUploadAndGetUrl(List<MultipartFile> multipartFiles, boolean isBigFile){
        List<FileDto> response = new ArrayList<>();

        if (isBigFile) {
            //TODO: 추후 영상 같은 이미지에 비해 고용량 업로드가 필요할시
            // 로딩바 구현을위해 Amazons3 로 구현
        } else {
            for (MultipartFile file : multipartFiles) {
                PutObjectRequest putObjectRequest = makePutObjectRequest(file);
                amazonS3Client.putObject(putObjectRequest);
                String url = amazonS3Client.getUrl(bucketName, putObjectRequest.getKey()).toString();
                response.add(new FileDto(file.getOriginalFilename(), url));
            }
        }
        return response;
    }


    private PutObjectRequest makePutObjectRequest(MultipartFile file) {
        String storageFileName = makeStorageFileName(Objects.requireNonNull(file.getOriginalFilename()));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            return new PutObjectRequest(bucketName, storageFileName, file.getInputStream(), objectMetadata);
        }catch (IOException ioException){
            throw new StorageUpdateFailedException.ExceptionBuilder()
                    .request(file.getOriginalFilename()).customMessage("File Upload Failed").build();
        }
    }

    //업로드 고유 파일명 생성
    private String makeStorageFileName(String originalFileName){
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        return UUID.randomUUID() + "." + extension;
    }

    //업로드 취소 (삭제)
    public void uploadCancel(List<String> fileUrls) {
        try{
            for(String url : fileUrls){

                String[] parts = url.split("/");
                String key = parts[parts.length - 1];
                amazonS3Client.deleteObject(bucketName,key);
            }
        }catch (AmazonS3Exception e){
            throw new StorageUpdateFailedException.ExceptionBuilder()
                    .request(fileUrls.toString())
                    .customMessage("File Delete Failed")
                    .systemMessage(e.getMessage()).build();
        }
    }

}
