package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.service.AmazonS3Service;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 파일과 관련된 컨트롤러
 * 
 * 1.재고관리 게시판 파일 업로드
 * 2.S3 파일 삭제
 *
 * @author Chanos
 * @since 2022-05-17
 */

@Api(tags="파일")
@RestController
@RequiredArgsConstructor
public class FileController {

    private final AmazonS3Service amazonS3Service;

    /*1.재고관리 게시판 파일 업로드*/
    @Operation(summary = "재고 사진 업로드", description = "재고 관리 게시판 사진 업로드")
    @PostMapping("/board/stock/upload/file/")
    public UploadStockResponseDto uploadStock(@RequestParam("file")MultipartFile file) {
        Map<String, String> fileInfo = amazonS3Service.uploadToS3(file, "stock");
        return new UploadStockResponseDto(fileInfo.get("fileName"),fileInfo.get("filePath"));
    }

    /*2.S3 파일 삭제*/
    @Operation(summary = "파일 삭제", description = "S3 Bucket에 업로드 된 파일 삭제")
    @DeleteMapping("/board/stock/delete/file/")
    public ResponseEntity deleteStock(@RequestParam("fileName") String fileName) {
        amazonS3Service.deleteFile(fileName);
        return new ResponseEntity("Delete file success", HttpStatus.OK);
    }

    /***--DTO--***/
    @Data @AllArgsConstructor
    static class UploadStockResponseDto {
        private String productFileName;
        private String productFilePath;
    }
}
