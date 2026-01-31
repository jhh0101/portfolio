package com.portfolio.auctionmarket.global.s3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucket;

    public S3Service(S3Client s3Client, @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public String uploadFile(MultipartFile file, String folderName) {
        // 1. 파일명 중복 방지를 위한 UUID 생성
        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // 2. 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            // 3. S3로 파일 전송
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 4. 저장된 파일의 공개 URL 반환
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fileName)).toString();

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(String fileUrl) {

        String key = extractKeyFromUrl(fileUrl); // URL에서 S3 Key(폴더명+파일명)만 추출하는 메서드
        String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(decodedKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("S3 파일 삭제 완료: " + decodedKey);
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        // 버킷명 뒤의 경로만 남기도록 로직 작성 (프로젝트 설정에 따라 다름)
        return fileUrl.substring(fileUrl.lastIndexOf(bucket) + bucket.length() + 1);
    }
}
