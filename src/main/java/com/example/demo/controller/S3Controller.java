package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.CloudFrontCookieGenerator;

import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Client s3Client;
    private final String bucket;

    public S3Controller(S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @GetMapping("/list")
    public List<String> listObjects() {
        var request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();

        return s3Client.listObjectsV2(request)
                .contents()
                .stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    @GetMapping("/cookies")
    public Map<String, String> getCookies() throws Exception {
        CookiesForCannedPolicy cookiesForCannedPolicy = CloudFrontCookieGenerator.getCookiesForCannedPolicy(
                CloudFrontCookieGenerator.createRequestForCannedPolicy("test.cloudfront.net", "sample.txt",
                        "./.ssh/private-key.pem", "TestKeyPairId"));
        Map<String, String> cookies = new HashMap<>();
        cookies.put("expires", cookiesForCannedPolicy.expiresHeaderValue());
        cookies.put("keypair", cookiesForCannedPolicy.keyPairIdHeaderValue());
        cookies.put("signature", cookiesForCannedPolicy.signatureHeaderValue());

        return cookies;
    }
}
