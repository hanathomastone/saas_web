package com.kaii.dentix.global.common.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.CORSConfiguration;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final S3Client s3;

    @Value("${s3.storage.bucketName}")
    private String bucketName;

    @Value("${s3.storage.region}")
    private String region;

    // 업로드
    public String upload(MultipartFile file, String path, boolean isTime) throws IOException {
        String originFileName = file.getOriginalFilename();
        String fileName = isTime
                ? originFileName.substring(0, originFileName.lastIndexOf('.')) + "_" + System.currentTimeMillis() +
                "." + originFileName.substring(originFileName.lastIndexOf('.') + 1)
                : originFileName;
        String filePath = path + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .contentType(file.getContentType())
//                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        // 업로드 결과 경로 (공개 버킷 기준)
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + filePath;
    }

    // 추가: 삭제/다운로드/기타 메서드도 동일 패턴으로 사용
    public byte[] download(String key) throws IOException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (SdkException e) {
            throw new IOException("S3 파일 다운로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // AWSS3Service 클래스 내에 추가
    public String getPresignedUrl(String key, int expireMinutes) {
        // region, bucketName은 기존과 같이 @Value로 받아와야 함

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expireMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        String url = presigner.presignGetObject(presignRequest).url().toString();
        presigner.close(); // presigner 리소스 닫기
        return url;
    }

    /**
     * S3 파일 업로드 및 삭제
     * @param file - 업로드 파일 정보
     * @param key - 파일 경로
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String uploadAndDelete(byte[] file, String path, String key) throws IOException, InterruptedException {
        // 1. 기존 파일 삭제 (key가 null 아니면)
        if (key != null) {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3.deleteObject(deleteRequest);
        }

        // 2. 새 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(path)
                .contentType("application/octet-stream") // 필요시 contentType 수정
                .acl("public-read") // 공개 버킷이면 (v2는 String으로 세팅)
                .build();

        s3.putObject(putObjectRequest, RequestBody.fromBytes(file));

        // 3. 업로드 결과 경로
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + path;
    }

    /**
     * Storage 파일 삭제
     */
    public void delete(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.deleteObject(deleteRequest);
    }

    /**
     * CORS 설정
     */
    public void setCors(String bucketName, List<String> domains) {
        CORSRule corsRule = CORSRule.builder()
                .allowedMethods("GET", "PUT", "POST")
                .allowedOrigins(domains)
                .allowedHeaders("*")
                .exposeHeaders("*")
                .maxAgeSeconds(3000)
                .build();

        CORSConfiguration corsConfig = CORSConfiguration.builder()
                .corsRules(corsRule)
                .build();

        PutBucketCorsRequest corsRequest = PutBucketCorsRequest.builder()
                .bucket(bucketName)
                .corsConfiguration(corsConfig)
                .build();

        s3.putBucketCors(corsRequest);
    }

}
