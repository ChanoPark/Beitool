package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.service.AmazonS3Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 파일과 관련된 컨트롤러
 * @author Chanos
 * @since 2022-05-17
 */
@RestController
@RequiredArgsConstructor
public class FileController {

    private final AmazonS3Service amazonS3Service;

    /*재고관리 게시판 파일 업로드*/
    @PostMapping("/board/stock/upload/file/")
    public UploadStockResponseDto uploadStock(@RequestParam("file")MultipartFile file) {
        Map<String, String> fileInfo = amazonS3Service.uploadToS3(file, "stock");
        return new UploadStockResponseDto(fileInfo.get("fileName"),fileInfo.get("filePath"));
    }

    /*S3 파일 삭제*/
    @DeleteMapping("/board/stock/delete/file/")
    public ResponseEntity deleteStock(@RequestBody Map<String, String> param) {
        amazonS3Service.deleteFile(param.get("fileName"));
        return new ResponseEntity("Delete file success", HttpStatus.OK);
    }

    /***--DTO--***/
    @Data @AllArgsConstructor
    static class UploadStockResponseDto {
        private String productFileName;
        private String productFilePath;
    }
}
