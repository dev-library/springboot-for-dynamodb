package com.example.dynamodbdemo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class DynamoDBConfiguration {

    @Value("${aws.dynamodb.endpoint:#{null}}")
    private String endPoint;

    @Value("${aws.region:ap-northeast-2}")
    private String region;

    @Value("${aws.accessKey:#{null}}")
    private String accessKey;

    @Value("${aws.secretKey:#{null}}")
    private String secretKey;

    @Value("${aws.dynamodb.local:false}")
    private boolean isLocal;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .withTableNameOverride(null)
                .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
                .build();

        return new DynamoDBMapper(amazonDynamoDB(), mapperConfig);
    }

    @Primary
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        // AWS 클라우드 환경에서는 기본 자격 증명 체인 사용 (IAM Role, Profile 등)
        if (!isLocal && (accessKey == null || accessKey.trim().isEmpty())) {
            return DefaultAWSCredentialsProviderChain.getInstance();
        }
        // 로컬 환경이나 명시적으로 키가 제공된 경우 정적 자격 증명 사용
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider());

        // 로컬 DynamoDB 사용 시 엔드포인트 설정
        if (isLocal && endPoint != null && !endPoint.trim().isEmpty()) {
            builder.withEndpointConfiguration(
                    new com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(endPoint, region)
            );
        } else {
            // AWS 클라우드 환경에서는 리전만 설정
            builder.withRegion(Regions.fromName(region));
        }

        return builder.build();
    }
}