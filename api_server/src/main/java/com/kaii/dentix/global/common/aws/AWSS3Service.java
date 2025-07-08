package com.kaii.dentix.global.common.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final S3Client s3;

    @Value("${s3.storage.bucketName}")
    private String bucketName;

    @Value("${s3.storage.region}")
    private String region;

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
}
