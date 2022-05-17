package com.beitool.beitool.api.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * AWS S3에 파일 업로드 등을 하기 위함.
 * BoardServiceImpl에 계속 작성하면 코드가 너무 길어져서 분리함.
 * 로컬에 파일을 저장하고, 이 파일을 S3에 업로드 한 뒤, 로컬에 있는 파일 삭제
 * @author Chanos
 * @since 2022-05-16
 */
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Value("${cloud.aws.s3.filePath}")
    private String filePath;

    /*S3에 파일 업로드*/
    public Map<String, String> uploadToS3(MultipartFile file, String dirName) {
        //사진 이름은 현재 시간(밀리초)
        String nowTime = new SimpleDateFormat("YYYYMMDDHHmmss").format(System.currentTimeMillis());
        String fileName = dirName + "/" + nowTime; //S3에 저장될 파일 이름
        //사진에 대한 정보 추가
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        //S3에 업로드
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch(IOException e) {
            System.out.println("***파일 업로드 실패");
            e.printStackTrace();
        }

        Map<String, String> fileInfo = new HashMap<>();
        fileInfo.put("fileName", fileName);
        fileInfo.put("filePath", filePath+fileName);

        return fileInfo;
    }

    /*S3의 파일 삭제*/
    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
