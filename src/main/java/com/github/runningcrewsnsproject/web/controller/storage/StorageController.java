package com.github.runningcrewsnsproject.web.controller.storage;


import com.github.runningcrewsnsproject.service.storage.StorageService;
import com.github.runningcrewsnsproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.runningcrewsnsproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController implements StorageControllerDocs {
    private final StorageService storageService;

    //한개 또는 여러개 업로드
    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomSuccessResponse> uploadMultipleFiles(@RequestPart("files") List<MultipartFile> multipartFiles,
                                                                     @RequestParam String directory,
                                                                     @RequestParam(required = false, name = "big") boolean isBigFile) {
        CustomSuccessResponse response = new CustomSuccessResponse.SuccessDetail()
                .responseData(storageService.fileUploadAndGetUrl(multipartFiles, isBigFile, directory))
                .httpStatus(HttpStatus.CREATED)
                .message("파일 업로드 성공").build();

        return new ResponseEntity<>(response, response.getSuccess().getHttpStatus());
    }


    @DeleteMapping//여러개 파일 업로드 취소 (삭제)
    public CustomSuccessResponse deleteMultipleFiles(@RequestParam(value = "delete-urls") List<String> fileUrls) {
        storageService.uploadCancel(fileUrls);
        return new CustomSuccessResponse
                .SuccessDetail().message("파일 삭제 성공").build();
    }

    //여러개 파일 수정
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomSuccessResponse modifyMultipleFiles(@RequestParam(value = "delete-urls") List<String> deleteFileUrls,
                                                     @RequestPart("files") List<MultipartFile> multipartFiles,
                                                     @RequestParam String directory,
                                                     @RequestParam(required = false, name = "big") boolean isBigFile) {
        List<FileDto> response = storageService.fileUploadAndGetUrl(multipartFiles, isBigFile, directory);
        storageService.uploadCancel(deleteFileUrls);
        return new CustomSuccessResponse.SuccessDetail()
                .responseData(response)
                .message("파일 수정 성공").build();
    }

}
