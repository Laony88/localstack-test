package com.example.demo.util;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

/**
 * # RSA 개인 키 생성
 * openssl genpkey -algorithm RSA -out private-key.pem -pkeyopt
 * rsa_keygen_bits:2048
 * # 공개 키 추출
 * openssl rsa -pubout -in private-key.pem -out public-key.pem
 */
public class CloudFrontCookieGenerator {
    private static final CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

    public static CannedSignerRequest createRequestForCannedPolicy(String distributionDomainName,
            String fileNameToUpload,
            String privateKeyFullPath, String publicKeyId) throws Exception {
        String protocol = "https";
        String resourcePath = "/" + fileNameToUpload;

        String cloudFrontUrl = new URL(protocol, distributionDomainName, resourcePath).toString();
        Instant expirationDate = Instant.now().plus(7, ChronoUnit.DAYS);
        Path path = Paths.get(privateKeyFullPath);

        return CannedSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(path)
                .keyPairId(publicKeyId)
                .expirationDate(expirationDate)
                .build();
    }

    public static CookiesForCannedPolicy getCookiesForCannedPolicy(CannedSignerRequest cannedSignerRequest) {
        return cloudFrontUtilities
                .getCookiesForCannedPolicy(cannedSignerRequest);
    }
}
