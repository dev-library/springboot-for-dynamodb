package com.example.dynamodbdemo.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.example.dynamodbdemo.customer.domain.Customer;
import com.example.dynamodbdemo.music.domain.MusicCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Customer Application에서 사용할 customer 테이블을 자동으로 생성하는 클래스
 * DynamodbDemoApplicaiton 시작할때 자동으로 실행되어 customer 테이블을 생성
 * applicaiton.properties 파일에 있는 dynamodb 정보를 환경에 맞게 수정 필요
 */
@Component
public class TableCreateRunner implements CommandLineRunner {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    void createTable() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        // Customer 테이블 생성
        CreateTableRequest customerTableRequest = dynamoDBMapper
                .generateCreateTableRequest(Customer.class);
        customerTableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(5L, 5L));
        
        // GSI에 대한 처리량 설정
        if (customerTableRequest.getGlobalSecondaryIndexes() != null) {
            customerTableRequest.getGlobalSecondaryIndexes().forEach(gsi -> 
                gsi.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
            );
        }

        // MusicCollection 테이블 생성
        CreateTableRequest musicCollectionTableRequest = dynamoDBMapper
                .generateCreateTableRequest(MusicCollection.class);
        musicCollectionTableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(5L, 5L));
        
        // GSI에 대한 처리량 설정
        if (musicCollectionTableRequest.getGlobalSecondaryIndexes() != null) {
            musicCollectionTableRequest.getGlobalSecondaryIndexes().forEach(gsi -> 
                gsi.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
            );
        }

        TableUtils.createTableIfNotExists(amazonDynamoDB, customerTableRequest);
        TableUtils.createTableIfNotExists(amazonDynamoDB, musicCollectionTableRequest);
        
        System.out.println("DynamoDB 테이블이 성공적으로 생성되었습니다.");
        System.out.println("- customer 테이블 (GSI: email-index, lastName-index)");
        System.out.println("- MusicCollection 테이블 (GSI: album-index)");
    }

    void deleteTable() {
        DeleteTableRequest customerTableRequest = dynamoDBMapper.generateDeleteTableRequest(Customer.class);
        TableUtils.deleteTableIfExists(amazonDynamoDB, customerTableRequest);

        DeleteTableRequest musicCollectionTable = dynamoDBMapper.generateDeleteTableRequest(MusicCollection.class);
        TableUtils.deleteTableIfExists(amazonDynamoDB, musicCollectionTable);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            deleteTable();
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}