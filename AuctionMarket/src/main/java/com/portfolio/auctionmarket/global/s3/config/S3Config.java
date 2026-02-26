package com.portfolio.auctionmarket.global.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Bean
//    public S3Client s3Client() {
//        return S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(DefaultCredentialsProvider.create())
//                 .endpointOverride(URI.create("http://localhost:4566")) // LocalStack 사용 시 주석 해제
//                 .forcePathStyle(true) // LocalStack 사용 시 주석 해제
//                .build();
//    }

    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                // 핵심: LocalStack 주소로 강제 고정
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                // LocalStack 사용 시 경로 스타일 설정을 true로 하는 것이 안정적입니다
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public CommandLineRunner initS3Bucket(S3Client s3Client) {
        return args -> {
            String bucketName = "my-test-bucket"; // application.yml의 값과 일치
            try {
                // 1. 버킷이 이미 있는지 확인
                s3Client.headBucket(b -> b.bucket(bucketName));
                System.out.println("✅ 로컬스택 S3 버킷 확인됨: " + bucketName);
            } catch (software.amazon.awssdk.services.s3.model.NoSuchBucketException e) {
                // 2. 없으면 새로 생성
                s3Client.createBucket(b -> b.bucket(bucketName));
                System.out.println("🚀 로컬스택 S3 버킷 생성 완료: " + bucketName);
            } catch (Exception e) {
                // 3. 다른 에러 발생 시 로그 출력
                System.err.println("❌ S3 초기화 실패: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
