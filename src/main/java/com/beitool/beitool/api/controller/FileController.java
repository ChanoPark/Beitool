package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public String uploadStock(@RequestParam("file")MultipartFile file) {
        return amazonS3Service.uploadToS3(file, "stock");
    }

    /*S3 파일 삭제*/
    @PostMapping("/board/stock/delete/file/")
    public void deleteStock(@RequestBody Map<String, String> param) {
        amazonS3Service.deleteFile(param.get("fileName"));
    }
}
