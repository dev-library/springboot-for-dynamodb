package com.example.dynamodbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
    org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class
})
public class DynamodbDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamodbDemoApplication.class, args);
    }
}
