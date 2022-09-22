package com.project.raidho.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.raidho.Util.CommonUtils;
import com.project.raidho.domain.ResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.net.URLDecoder;

@Service
@RequiredArgsConstructor
public class S3Service  {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public String upload(MultipartFile file) throws IOException {

        String fileName = CommonUtils.buildFileName(file.getOriginalFilename());// 파일이름
        long size = file.getSize(); // 파일 크기

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());  // 이게 머지? : 파일타입
        objectMetadata.setContentLength(size); //파일크기
        InputStream inputStream = file.getInputStream();   // 실제 데이터를 넣어준다
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));  // S3 저장 및 권한설정
        String imageUrl = amazonS3.getUrl(bucket, fileName).toString();
        imageUrl = URLDecoder.decode(imageUrl,"UTF-8");
        return imageUrl;
        // URL 대입!, URL 변환시 한글깨짐
    }

    public ResponseDto<?> editMyPage(String fileName) {

        if (fileName !=null){
            amazonS3.deleteObject(new DeleteObjectRequest(bucket,fileName));
        }
        return ResponseDto.fail(404,"이미지가 없습니다.");
    }

}