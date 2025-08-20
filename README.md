# AWS DynamoDB Spring Boot Demo

이 프로젝트는 Spring Boot와 AWS DynamoDB를 연동하여 고객(Customer)과 음악 컬렉션(MusicCollection) 데이터를 관리하는 REST API 데모 애플리케이션입니다.

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [DynamoDB 테이블 구조](#dynamodb-테이블-구조)
- [인덱스 설계](#인덱스-설계)
- [설정 가이드](#설정-가이드)
- [AWS 환경 설정](#aws-환경-설정)
- [로컬 개발 환경 설정](#로컬-개발-환경-설정)
- [API 엔드포인트](#api-엔드포인트)
- [실행 방법](#실행-방법)
- [DynamoDB 개념 정리](#dynamodb-개념-정리)

## 🎯 프로젝트 개요

본 프로젝트는 다음과 같은 기능을 제공합니다:
- 고객 정보 CRUD 작업
- 음악 컬렉션 관리
- AWS DynamoDB와의 완전한 연동
- 로컬 DynamoDB와 AWS 클라우드 DynamoDB 모두 지원
- Swagger UI를 통한 API 문서화

## 🛠 기술 스택

- **Java 11**
- **Spring Boot 2.7.7**
- **AWS SDK for Java 1.12.376**
- **Spring Cloud AWS 2.2.6**
- **Swagger/OpenAPI 3.0**
- **AWS DynamoDB**

## 📁 프로젝트 구조

```
src/main/java/com/example/dynamodbdemo/
├── config/
│   ├── DynamoDBConfiguration.java    # DynamoDB 설정
│   └── SwaggerConfig.java            # Swagger 설정
├── customer/
│   ├── controller/
│   │   └── CustomerController.java   # 고객 REST API
│   ├── domain/
│   │   ├── Customer.java            # 고객 엔티티
│   │   └── Address.java             # 주소 임베디드 객체
│   ├── repository/
│   │   └── CustomerRepository.java   # 고객 데이터 접근 계층
│   └── service/
│       └── CustomerService.java     # 고객 비즈니스 로직
├── music/
│   ├── controller/
│   │   └── MusicCollectionController.java  # 음악 REST API
│   ├── domain/
│   │   └── MusicCollection.java           # 음악 엔티티
│   ├── repository/
│   │   └── MusicCollectionRepository.java  # 음악 데이터 접근 계층
│   └── service/
│       └── MusicCollectionService.java    # 음악 비즈니스 로직
├── util/
│   └── TableCreateRunner.java        # 테이블 자동 생성 유틸리티
└── DynamodbDemoApplication.java      # 메인 애플리케이션
```

## 🗄 DynamoDB 테이블 구조

### 1. Customer 테이블

| 속성명 | 타입 | 키 타입 | 설명 |
|--------|------|---------|------|
| customerId | String | Partition Key (PK) | 고객 고유 ID (자동 생성) |
| firstName | String | - | 이름 |
| lastName | String | - | 성 |
| email | String | GSI PK | 이메일 주소 |
| address | Object | - | 주소 정보 (임베디드) |
| createdAt | String | GSI SK | 생성 시간 |
| lastNameIndex | String | GSI PK | 성 검색용 인덱스 |

**Address 임베디드 객체:**
- line1: 주소 첫 번째 줄
- city: 도시
- country: 국가

### 2. MusicCollection 테이블

| 속성명 | 타입 | 키 타입 | 설명 |
|--------|------|---------|------|
| artist | String | Partition Key (PK) | 아티스트명 |
| songTitle | String | Sort Key (SK) | 곡 제목 |
| albumTitle | String | - | 앨범 제목 |
| albumTitleIndex | String | GSI PK | 앨범별 검색용 인덱스 |
| releaseYear | String | GSI SK | 발매년도 |

## 🔍 인덱스 설계

### Customer 테이블 인덱스

1. **email-index (GSI)**
   - Partition Key: email
   - Sort Key: createdAt
   - 용도: 이메일로 고객 검색, 생성일 기준 정렬

2. **lastName-index (GSI)**
   - Partition Key: lastNameIndex
   - 용도: 성(lastName)으로 고객 검색

### MusicCollection 테이블 인덱스

1. **album-index (GSI)**
   - Partition Key: albumTitleIndex
   - Sort Key: releaseYear
   - 용도: 앨범별 곡 검색, 발매년도 기준 정렬

## ⚙️ 설정 가이드

### application.properties 설정

```properties
# ===============================
# AWS DynamoDB Configuration
# ===============================

# AWS Region (기본값: ap-northeast-2)
aws.region=ap-northeast-2

# 로컬 DynamoDB 사용 여부 (true: 로컬, false: AWS 클라우드)
aws.dynamodb.local=false

# 로컬 DynamoDB 엔드포인트 (로컬 사용 시에만 필요)
# aws.dynamodb.endpoint=http://localhost:8000

# AWS 자격 증명 (선택사항)
# AWS 클라우드 환경에서는 IAM Role, AWS Profile, 환경변수 등을 통해 자격 증명을 제공할 수 있습니다.
# 명시적으로 키를 설정하려면 아래 주석을 해제하고 실제 값을 입력하세요.
# aws.accessKey=YOUR_ACCESS_KEY
# aws.secretKey=YOUR_SECRET_KEY
```

## ☁️ AWS 환경 설정

### 1. AWS 자격 증명 설정

AWS 클라우드에서 실행할 때는 다음 중 하나의 방법으로 자격 증명을 제공할 수 있습니다:

#### A. IAM Role (권장 - EC2, ECS, Lambda 등)
```properties
aws.dynamodb.local=false
aws.region=ap-northeast-2
# 자격 증명 설정 불필요 (IAM Role 자동 사용)
```

#### B. AWS Profile
```bash
# AWS CLI 설정
aws configure --profile myprofile
```

```properties
aws.dynamodb.local=false
aws.region=ap-northeast-2
# 환경변수로 프로필 지정: AWS_PROFILE=myprofile
```

#### C. 환경변수
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_DEFAULT_REGION=ap-northeast-2
```

#### D. 명시적 키 설정 (개발/테스트 환경만)
```properties
aws.dynamodb.local=false
aws.region=ap-northeast-2
aws.accessKey=YOUR_ACCESS_KEY
aws.secretKey=YOUR_SECRET_KEY
```

### 2. DynamoDB 테이블 생성

애플리케이션 실행 시 자동으로 테이블이 생성됩니다. 수동으로 생성하려면:

```bash
# Customer 테이블 생성
aws dynamodb create-table \
    --table-name customer \
    --attribute-definitions \
        AttributeName=customerId,AttributeType=S \
        AttributeName=email,AttributeType=S \
        AttributeName=createdAt,AttributeType=S \
        AttributeName=lastNameIndex,AttributeType=S \
    --key-schema \
        AttributeName=customerId,KeyType=HASH \
    --global-secondary-indexes \
        IndexName=email-index,KeySchema=[{AttributeName=email,KeyType=HASH},{AttributeName=createdAt,KeyType=RANGE}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5} \
        IndexName=lastName-index,KeySchema=[{AttributeName=lastNameIndex,KeyType=HASH}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5} \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region ap-northeast-2

# MusicCollection 테이블 생성
aws dynamodb create-table \
    --table-name MusicCollection \
    --attribute-definitions \
        AttributeName=artist,AttributeType=S \
        AttributeName=songTitle,AttributeType=S \
        AttributeName=albumTitleIndex,AttributeType=S \
        AttributeName=releaseYear,AttributeType=S \
    --key-schema \
        AttributeName=artist,KeyType=HASH \
        AttributeName=songTitle,KeyType=RANGE \
    --global-secondary-indexes \
        IndexName=album-index,KeySchema=[{AttributeName=albumTitleIndex,KeyType=HASH},{AttributeName=releaseYear,KeyType=RANGE}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5} \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region ap-northeast-2
```

## 🏠 로컬 개발 환경 설정

### 1. 로컬 DynamoDB 설치 및 실행

```bash
# Docker를 사용한 로컬 DynamoDB 실행
docker run -p 8000:8000 amazon/dynamodb-local

# 또는 JAR 파일 다운로드 후 실행
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 8000
```

### 2. 로컬 환경 설정

```properties
# 로컬 개발 환경 설정
aws.dynamodb.local=true
aws.dynamodb.endpoint=http://localhost:8000
aws.region=ap-northeast-2
aws.accessKey=localkey
aws.secretKey=localsecret
```

### 3. 로컬 DynamoDB 확인

```bash
# 테이블 목록 확인
aws dynamodb list-tables --endpoint-url http://localhost:8000

# 테이블 스캔
aws dynamodb scan --table-name customer --endpoint-url http://localhost:8000
```

## 🔌 API 엔드포인트

### Customer API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/customers` | 모든 고객 조회 |
| GET | `/customers/{id}` | 특정 고객 조회 |
| POST | `/customers` | 새 고객 생성 |
| PUT | `/customers/{id}` | 고객 정보 수정 |
| DELETE | `/customers/{id}` | 고객 삭제 |

### MusicCollection API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/music` | 모든 음악 조회 |
| GET | `/music/{artist}/{songTitle}` | 특정 곡 조회 |
| POST | `/music` | 새 곡 추가 |
| PUT | `/music/{artist}/{songTitle}` | 곡 정보 수정 |
| DELETE | `/music/{artist}/{songTitle}` | 곡 삭제 |

### Swagger UI

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
- http://localhost:8080/swagger-ui/index.html

## 🚀 실행 방법

### 1. 프로젝트 클론 및 빌드

```bash
git clone <repository-url>
cd aws_dynamodb
./gradlew build
```

### 2. 애플리케이션 실행

```bash
# Gradle을 사용한 실행
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/dynamodb-demo-0.0.1-SNAPSHOT.jar
```

### 3. 테스트 요청

```bash
# 고객 생성
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "홍",
    "lastName": "길동",
    "email": "hong@example.com",
    "address": {
      "line1": "서울시 강남구",
      "city": "서울",
      "country": "대한민국"
    }
  }'

# 음악 추가
curl -X POST http://localhost:8080/music \
  -H "Content-Type: application/json" \
  -d '{
    "artist": "BTS",
    "songTitle": "Dynamite",
    "albumTitle": "BE",
    "releaseYear": "2020"
  }'
```

## 📚 DynamoDB 개념 정리

### 1. 기본 개념

#### Primary Key (기본 키)
- **Partition Key (Hash Key)**: 데이터를 분산 저장하는 기준이 되는 키
- **Sort Key (Range Key)**: 같은 파티션 내에서 데이터를 정렬하는 키
- **Composite Key**: Partition Key + Sort Key 조합

#### 예시:
```java
@DynamoDBTable(tableName = "MusicCollection")
public class MusicCollection {
    @DynamoDBHashKey        // Partition Key
    private String artist;
    
    @DynamoDBRangeKey       // Sort Key
    private String songTitle;
}
```

### 2. 인덱스 (Index)

#### Local Secondary Index (LSI)
- 같은 Partition Key를 사용하지만 다른 Sort Key를 가지는 인덱스
- 테이블 생성 시에만 정의 가능
- 파티션당 10GB 제한

#### Global Secondary Index (GSI)
- 완전히 다른 Partition Key와 Sort Key를 가질 수 있는 인덱스
- 언제든지 생성/삭제 가능
- 별도의 읽기/쓰기 용량 단위 필요

#### 예시:
```java
@DynamoDBAttribute
@DynamoDBIndexHashKey(globalSecondaryIndexName = "email-index")
private String email;

@DynamoDBAttribute
@DynamoDBIndexRangeKey(globalSecondaryIndexName = "email-index")
private String createdAt;
```

### 3. 쿼리 패턴

#### Query vs Scan
- **Query**: Primary Key나 Index를 사용한 효율적인 검색
- **Scan**: 전체 테이블을 순회하는 비효율적인 검색 (가급적 사용 금지)

#### Query 예시:
```bash
# Primary Key로 쿼리
aws dynamodb query \
    --table-name MusicCollection \
    --key-condition-expression "artist = :artist" \
    --expression-attribute-values '{":artist":{"S":"BTS"}}'

# GSI로 쿼리
aws dynamodb query \
    --table-name customer \
    --index-name email-index \
    --key-condition-expression "email = :email" \
    --expression-attribute-values '{":email":{"S":"hong@example.com"}}'
```

### 4. 성능 최적화 팁

1. **적절한 Partition Key 선택**: 데이터가 고르게 분산되도록 설계
2. **Hot Partition 방지**: 특정 파티션에 읽기/쓰기가 집중되지 않도록 주의
3. **GSI 활용**: 다양한 쿼리 패턴을 지원하기 위해 적절한 GSI 설계
4. **Batch 작업 활용**: 여러 아이템을 한 번에 처리할 때 BatchGet/BatchWrite 사용
5. **일관된 읽기**: 강한 일관성이 필요한 경우에만 Consistent Read 사용

### 5. 비용 최적화

1. **On-Demand vs Provisioned**: 트래픽 패턴에 따라 적절한 요금 모드 선택
2. **TTL 활용**: 자동으로 만료되는 데이터에 Time To Live 설정
3. **압축**: 큰 데이터는 압축하여 저장
4. **불필요한 GSI 제거**: 사용하지 않는 인덱스는 삭제

## 🔧 트러블슈팅

### 1. 자격 증명 오류
```
AmazonClientException: Unable to load AWS credentials
```
**해결방법**: AWS 자격 증명이 올바르게 설정되어 있는지 확인

### 2. 테이블 생성 실패
```
ResourceInUseException: Table already exists
```
**해결방법**: 기존 테이블 삭제 후 재생성 또는 TableCreateRunner 수정

### 3. 인덱스 쿼리 오류
```
ValidationException: Query key condition not supported
```
**해결방법**: GSI 키 조건이 올바른지 확인, Partition Key는 필수

## 📝 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 문의사항

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.
