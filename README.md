# moviesite — 오늘의 영화

> 실시간 박스오피스와 영화 정보를 한눈에 확인할 수 있는 영화 정보 통합 서비스

🔗 **[배포 사이트](https://movie-cinemasite.onrender.com/)** | 📂 **[GitHub](https://github.com/hanlee99/movie-cinemaSite)**

---

## 📌 프로젝트 소개

**KOBIS(영화진흥위원회)**와 **KMDB(한국영화데이터베이스)** API를 통합하여  
실시간 박스오피스 순위와 영화 상세 정보를 제공하는 웹 서비스입니다.

### 주요 특징
- 🎬 **일일 박스오피스** - KOBIS API를 통한 실시간 순위 제공
- 📽️ **영화 상세 정보** - KMDB API 연동으로 포스터, 줄거리, 출연진 정보 제공
- 🏢 **전국 극장 정보** - 공공데이터포털의 영화상영관 표준데이터 활용
- 🔍 **영화 검색** - 제목 기반 실시간 검색

### 아키텍처
- **백엔드**: Spring Boot에서 외부 API 호출 및 데이터 가공
- **프론트엔드**: Thymeleaf 기반 SSR + JavaScript로 동적 콘텐츠 처리
- **데이터**: 외부 API 응답을 Adapter 패턴으로 내부 도메인 모델로 변환

> 🔗 **참고 API**
> - [KOBIS Open API](https://www.kobis.or.kr/kobisopenapi/homepg/main/main.do) - 박스오피스 순위
> - [KMDB 영화정보시스템](https://www.kmdb.or.kr/main) - 영화 상세 정보
> - [공공데이터포털](https://www.data.go.kr/data/15107749/standard.do) - 영화상영관 표준데이터

---

## 🛠 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| **Backend** | Java 21, Spring Boot 3.x, Spring Data JPA, Lombok |
| **Frontend** | Thymeleaf, TailwindCSS, DaisyUI, JavaScript (ES6+) |
| **Database** | PostgreSQL (Production), H2 (Development) |
| **Build Tool** | Gradle |
| **External APIs** | KOBIS API, KMDB API |
| **Security** | Spring Security, Bucket4j (Rate Limiting) |
| **Caching** | Caffeine Cache |

---

## 🚀 배포 & 실행 방법

### ⚠️ 첫 접속 시 안내
무료 호스팅(Render Free Tier) 사용으로 **첫 방문 시 2-3분** 소요될 수 있습니다.  
서버가 활성화되면 평균 응답 속도는 **1초 이내**입니다.

### 온라인 접속 (배포 버전)
🔗 **https://movie-cinemasite.onrender.com/**
- PostgreSQL 기반 운영 중
- 2025년 영화 데이터 기반
- 즉시 접속 가능

### 로컬 실행 (개발 환경)

#### 1. 레포지토리 클론
```bash
git clone https://github.com/hanlee99/movie-cinemaSite.git
cd movie-cinemaSite
```

#### 2. 환경변수 설정

**Windows**
```bash
setx KOBIS_API_KEY "발급받은_KOBIS_API키"
setx KMDB_API_KEY "발급받은_KMDB_API키"
```

**macOS / Linux**
```bash
export KOBIS_API_KEY="발급받은_KOBIS_API키"
export KMDB_API_KEY="발급받은_KMDB_API키"
```

또는 `application.properties`에서 직접 설정:
```properties
api.kobis.key=발급받은_KOBIS_API키
api.kmdb.key=발급받은_KMDB_API키
```

#### 3. 애플리케이션 실행
```bash
# Gradle 사용
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar
```

#### 4. 접속
```
http://localhost:8080
```

---

## ✨ 주요 기능

### 1. 일일 박스오피스
- KOBIS API를 통한 실시간 박스오피스 순위 제공
- Caffeine 캐시로 1일 TTL 적용
- 순위, 관객수, 매출액 정보 표시

### 2. 영화 정보 통합
- KMDB API 연동으로 영화 포스터 자동 수집
- 감독, 배우, 장르, 줄거리 등 상세 정보 제공
- DB 우선 검색 → KMDB API Fallback 전략

### 3. 영화 검색
- 제목 기반 실시간 검색 기능
- JPA LIKE 쿼리로 빠른 검색
- 검색 결과 페이징 처리

### 4. 현재 상영작 / 개봉 예정작
- 페이징 처리로 대용량 데이터 효율적 조회
- 개봉일 기준 자동 분류

### 5. 전국 극장 정보
- 공공데이터포털의 500+ 극장 정보 제공
- 브랜드별, 지역별 필터링 지원

---

## 📁 프로젝트 구조
```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── controller/         # REST API 엔드포인트
│   │   │   ├── service/            # 비즈니스 로직 & 트랜잭션 관리
│   │   │   ├── repository/         # JPA Repository
│   │   │   ├── entity/             # JPA Entity (Movie, Cinema 등)
│   │   │   ├── dto/                # 계층간 데이터 전송 객체
│   │   │   │   ├── movie/          # 영화 관련 DTO
│   │   │   │   └── cinema/         # 극장 관련 DTO
│   │   │   ├── external/           # 외부 API 연동
│   │   │   │   ├── adapter/        # API 어댑터
│   │   │   │   ├── kobis/          # KOBIS API Client
│   │   │   │   └── kmdb/           # KMDB API Client
│   │   │   ├── mapper/             # Entity ↔ DTO 변환
│   │   │   ├── exception/          # 예외 처리
│   │   │   └── config/             # Spring 설정
│   │   └── resources/
│   │       ├── data/               # 초기 데이터 (CSV)
│   │       ├── templates/          # Thymeleaf 템플릿
│   │       └── static/             # CSS, JS, Images
│   └── test/                       # 테스트 코드
├── build.gradle
└── README.md
```

---

## 🏗️ 아키텍처 설계

### Layered Architecture
```
Presentation Layer (Controller)
    ↓
Business Layer (Service)
    ↓ ↓ ↓
Adapter Layer | Mapper Layer | Persistence Layer
    ↓                              ↓
External APIs              PostgreSQL Database
```

### 주요 설계 패턴
- **Adapter Pattern**: 외부 API(KOBIS, KMDB)를 Adapter로 격리하여 Service 의존성 분리
- **DTO 변환 전략**: Entity와 DTO 명확히 분리로 계층간 결합도 최소화
- **Service 책임 분리**: MovieService(조회) / MovieSyncService(동기화)로 SRP 준수

---

## 📡 API 명세

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/` | 메인 페이지 (Thymeleaf) | - |
| GET | `/movies/{id}` | 영화 상세 정보 | id |
| GET | `/movies/search` | 영화 검색 | keyword |
| GET | `/movie/box-office/daily` | 일일 박스오피스 조회 | - |
| GET | `/movie/now-playing` | 현재 상영작 목록 | page, size |
| GET | `/movie/upcoming` | 개봉 예정작 목록 | page, size |

---

## 🔐 보안 구현

- **Spring Security**: 관리자 페이지 Basic Authentication
- **Rate Limiting**: Bucket4j로 API 분당 20회 제한 (API 남용 방지)
- **환경변수 관리**: API 키는 `.env`와 환경변수로 보호
- **SQL Injection 방지**: JPA PreparedStatement 사용
- **CORS 설정**: 필요한 도메인만 허용

---

## 🚀 배포 환경

- **Platform**: [Render](https://render.com/) (무료 Tier)
- **Database**: PostgreSQL (무료 Tier)
- **Runtime**: Docker
- **CI/CD**: GitHub 자동 연동 (main 브랜치 푸시 시 자동 배포)
- **Status**: 운영 중 ✅

### 데이터 동기화 전략

- **KOBIS API**: 요청 시마다 실시간 조회 (Caffeine 캐싱으로 1일 TTL)
- **KMDB API**: 초기 배포 시 일괄 동기화, 관리자 API를 통한 수동 갱신 가능
- **극장 정보**: 공공데이터포털 기반 정적 데이터

---

## 🎯 기술적 의사결정

### PostgreSQL 선택 이유
- 배포 환경(Render) 무료 PostgreSQL 지원
- H2보다 안정적인 운영 환경
- 실무 스택 경험 축적

### Service 계층 분리 (CQRS)
- **MovieService**: 조회 전용 (`@Transactional(readOnly=true)`)
- **MovieSyncService**: 수정/동기화 전용
- 성능 최적화 및 책임 분리

### Adapter Pattern 적용
- 외부 API 변경에 대한 영향 최소화
- Service 계층의 외부 의존성 제거
- 테스트 용이성 향상

---

## 🔧 트러블슈팅

### 1. 한글 인코딩 문제 (UTF-8 디코딩)
**문제**: KMDB API 응답에서 한글이 깨져서 나옴  
**원인**: JVM 파일 디코딩과 저장 인코딩 불일치  
**해결**: application.properties에서 UTF-8 강제 설정 + HttpURLConnection 헤더 설정  
**결과**: API 응답 정상화 ✅

### 2. 외부 API 데이터 정규화 및 매핑
**문제**: KMDB API 응답 필드명이 다르고, 앞뒤 공백이 있어서 KOBIS와 매칭 실패  
**원인**: @JsonProperty 미설정 + 데이터 정규화 부재  
**해결**: @JsonProperty로 필드명 매핑 + trim()으로 공백 제거 후 저장  
**결과**: API 와 db의 데이터 매칭 성공 ✅

### 3. N+1 쿼리 최적화
**문제**: 영화 상세 페이지에서 출연진 조회 시 N+1 쿼리 발생  
**해결**: 상황별로 다른 메서드 구현 (기본 조회 vs LEFT JOIN FETCH)  
**결과**: 쿼리 50% 감소, 응답 시간 30% 개선 ✅

### 4. 페이징과 필터링 순서
**문제**: 페이징을 먼저 한 후 필터링하니 결과 개수 부족  
**원인**: 필터링 → 페이징 순서 미준수  
**해결**: Repository 메서드에서 필터링 조건을 쿼리에 포함시킨 후 페이징 적용  
**결과**: 정확한 페이징 결과 ✅

### 5. 외부 API 장애 대응
**문제**: KOBIS API 일시적 장애 시 서비스 중단  
**해결**: 예외 처리 + Caffeine 캐시로 최근 데이터 제공 (Fallback 전략)  
**결과**: 서비스 가용성 향상 ✅

---

## 📊 주요 성과

- N+1 쿼리 최적화: 8개 → 1개 (87% 감소)
- 캐싱 도입: 응답속도 1.77초 → 0.5초 (72% 개선)
- API 매칭률: 90% → 거의 100%

---

## 🧪 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 테스트 커버리지 리포트 생성
./gradlew test jacocoTestReport

# 리포트 확인
open build/reports/jacoco/test/html/index.html
```
### 테스트 구성
- **Service 계층 테스트**: MovieService 핵심 로직 검증
- **통합 테스트**: 외부 API 연동 확인
- **Entity 매핑 테스트**: DTO 변환 검증
---

## 📝 향후 개선 계획

- [ ] Swagger/SpringDoc을 통한 API 문서화
- [ ] Redis 캐싱 레이어 구현
- [ ] GitHub Actions를 통한 자동 데이터 동기화
- [ ] 사용자 인증 및 찜하기 기능
- [ ] 위치 기반 극장 검색 완성 (좌표 변환 EPSG:5174)

---

## 👨‍💻 개발자

**이한 (Lee Han)**
- 📧 Email: cahannon538@naver.com
- 💻 GitHub: [@hanlee99](https://github.com/hanlee99)
- 📱 Phone: 010-3845-9075

---

## 📄 라이선스

이 프로젝트는 개인 포트폴리오 목적으로 제작되었습니다.

---

## 🙏 감사의 말

- KOBIS Open API와 KMDB API를 제공해주신 영화진흥위원회와 한국영화데이터베이스에 감사드립니다.
- 공공데이터포털의 영화상영관 표준데이터를 활용했습니다.
