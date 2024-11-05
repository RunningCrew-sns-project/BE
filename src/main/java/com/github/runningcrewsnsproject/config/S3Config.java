package com.github.runningcrewsnsproject.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client defaultS3Client(){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(region))
                .build();
    }
    @Bean
    public AmazonS3Client amazonS3Client() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))
                )
                .withRegion(region)
                .build();
    }
}
