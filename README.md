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
- 🔐 **소셜 로그인** - 네이버/구글 OAuth2 인증 지원
- 📝 **관람기록 관리** - 별점, 극장, 한줄평 등 개인 관람 기록 저장
- ❤️ **찜 목록** - 보고 싶은 영화를 찜하여 마이페이지에서 관리
- 🔒 **동시성 제어** - Optimistic Locking으로 조회수/찜 개수 정확성 보장

### 아키텍처
- **백엔드**: Spring Boot에서 외부 API 호출 및 데이터 가공
- **프론트엔드**: Thymeleaf 기반 SSR + JavaScript로 동적 콘텐츠 처리
- **데이터**: 외부 API 응답을 Adapter 패턴으로 내부 도메인 모델로 변환

> 🔗 **참고 API**
> - [KOBIS Open API](https://www.kobis.or.kr/kobisopenapi/homepg/main/main.do) - 박스오피스 순위
> - [KMDB OPEN API](https://www.kmdb.or.kr/main) - 영화 상세 정보
> - [공공데이터포털](https://www.data.go.kr/data/15107749/standard.do) - 영화상영관 표준데이터
> - [카카오맵 API] - 영화관 위치 및 지도
---

## 🛠 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| **Backend** | Java 21, Spring Boot 3.x, Spring Data JPA, Lombok |
| **Frontend** | Thymeleaf, TailwindCSS, DaisyUI, JavaScript (ES6+) |
| **Database** | PostgreSQL (Production), H2 (Development) |
| **Build Tool** | Gradle |
| **External APIs** | KOBIS API, KMDB API, KAKAO API |
| **Security** | Spring Security, OAuth2 Client (Naver/Google), Bucket4j (Rate Limiting) |
| **Caching** | Caffeine Cache |
| **Concurrency** | JPA @Version (Optimistic Locking), Spring Retry |

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
setx KAKAO_API_KEY="발급받은_KAKAO_API키"
setx NAVER_CLIENT_ID "네이버_OAuth_클라이언트_ID"
setx NAVER_CLIENT_SECRET "네이버_OAuth_클라이언트_시크릿"
setx GOOGLE_CLIENT_ID "구글_OAuth_클라이언트_ID"
setx GOOGLE_CLIENT_SECRET "구글_OAuth_클라이언트_시크릿"
```

**macOS / Linux**
```bash
export KOBIS_API_KEY="발급받은_KOBIS_API키"
export KMDB_API_KEY="발급받은_KMDB_API키"
export KAKAO_API_KEY="발급받은_KAKAO_API키"
export NAVER_CLIENT_ID="네이버_OAuth_클라이언트_ID"
export NAVER_CLIENT_SECRET="네이버_OAuth_클라이언트_시크릿"
export GOOGLE_CLIENT_ID="구글_OAuth_클라이언트_ID"
export GOOGLE_CLIENT_SECRET="구글_OAuth_클라이언트_시크릿"
```

또는 `application.properties`에서 직접 설정:
```properties
api.kobis.key=발급받은_KOBIS_API키
api.kmdb.key=발급받은_KMDB_API키
api.kakao.key=발급받은_KAKAO_API키(자바스크립트 용)

# OAuth2 설정 (선택사항 - 소셜 로그인 사용 시)
spring.security.oauth2.client.registration.naver.client-id=네이버_클라이언트_ID
spring.security.oauth2.client.registration.naver.client-secret=네이버_시크릿
spring.security.oauth2.client.registration.google.client-id=구글_클라이언트_ID
spring.security.oauth2.client.registration.google.client-secret=구글_시크릿
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

#### 5. 개발자용 테스트 계정 (선택사항)

실제 서비스는 **네이버/구글 소셜 로그인**만 지원합니다.

**테스트 목적으로 로컬 계정을 생성하려면:**

1. **직접 회원가입**: http://localhost:8080/user/dev-register
2. **직접 로그인**: http://localhost:8080/user/dev-login

> ⚠️ `/user/dev-*` 경로는 개발/테스트 전용입니다.
> 프로덕션 배포 시에는 소셜 로그인만 활성화됩니다.

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
- 공공데이터포털의 433개 극장 정보 제공
- 브랜드별, 지역별 필터링 지원
- 카카오맵 API 연동으로 지도에서 극장 위치 확인

### 6. 사용자 맞춤 기능
- **관람기록**: 관람일, 극장, 별점(1-5), 한줄평 기록 가능
- **찜 목록**: 보고 싶은 영화를 저장하고 관리
- **마이페이지**: 관람기록과 찜 목록을 통합 관리

### 7. 동시성 제어
- **Optimistic Locking**: JPA @Version을 활용한 낙관적 락
- **자동 재시도**: Spring Retry로 충돌 시 최대 3회 재시도
- **조회수/찜 개수**: 동시 접근 시에도 정확한 카운팅 보장

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
│   │   │   ├── entity/             # JPA Entity (Movie, Cinema, Wishlist, WatchHistory, MovieStats 등)
│   │   │   ├── dto/                # 계층간 데이터 전송 객체
│   │   │   │   ├── movie/          # 영화 관련 DTO
│   │   │   │   └── cinema/         # 극장 관련 DTO
│   │   │   ├── external/           # 외부 API 연동
│   │   │   │   ├── adapter/        # API 어댑터
│   │   │   │   ├── kobis/          # KOBIS API Client
│   │   │   │   └── kmdb/           # KMDB API Client
│   │   │   ├── mapper/             # Entity ↔ DTO 변환
│   │   │   ├── security/           # Spring Security, OAuth2 설정
│   │   │   ├── exception/          # 예외 처리
│   │   │   └── config/             # Spring 설정 (Cache, Retry 등)
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
- **Optimistic Locking**: @Version을 이용한 낙관적 락으로 동시성 제어
- **재시도 패턴**: @Retryable로 충돌 시 자동 재시도 (최대 3회, 50ms 간격)

---

## 📡 API 명세

### 영화 정보 API
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/` | 메인 페이지 (Thymeleaf) | - |
| GET | `/movies/{id}` | 영화 상세 정보 | id |
| GET | `/movies/search` | 영화 검색 | keyword |
| GET | `/movie/box-office/daily` | 일일 박스오피스 조회 | - |
| GET | `/movie/now-playing` | 현재 상영작 목록 | page, size |
| GET | `/movie/upcoming` | 개봉 예정작 목록 | page, size |

### 사용자 기능 API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/user/watch-history` | 관람기록 페이지 | Required |
| GET | `/api/watch-history` | 내 관람기록 조회 | Required |
| POST | `/api/watch-history` | 관람기록 추가 | Required |
| DELETE | `/api/watch-history/{id}` | 관람기록 삭제 | Required |
| GET | `/user/wishlist` | 찜 목록 페이지 | Required |
| GET | `/api/wishlist` | 내 찜 목록 조회 | Required |
| GET | `/api/wishlist/{movieId}` | 찜 상태 확인 | Required |
| POST | `/api/wishlist/{movieId}` | 찜 토글 (추가/삭제) | Required |

### 극장 정보 API
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/api/cinema/search` | 극장 검색 | keyword |

---

## 🚀 배포 환경

- **Platform**: [Render](https://render.com) (무료 Tier)
- **Database**: PostgreSQL (무료 Tier)
- **Runtime**: Docker
- **CI/CD**: GitHub 자동 연동 (main 브랜치 푸시 시 자동 배포)
- **Status**: 운영 중 ✅

### 데이터 동기화 전략

- **KOBIS API**: 요청 시마다 실시간 조회 (Caffeine 캐싱으로 1일 TTL)
- **KMDB API**: 초기 배포 시 일괄 동기화, 관리자 API를 통한 수동 갱신 가능
- **극장 정보**: 공공데이터포털 기반 정적 데이터 CSV파일 초기화로 실행시 자동 초기

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
- [x] 사용자 인증 및 찜하기 기능 ✅
- [x] 관람기록 관리 기능 ✅
- [x] Optimistic Locking을 통한 동시성 제어 ✅
- [ ] 사용자 통계 (월별 관람 횟수, 장르 선호도 등)
- [ ] 영화 추천 알고리즘 (관람기록/찜 기반)

---

## 📄 라이선스

이 프로젝트는 개인 포트폴리오 목적으로 제작되었습니다.

---

## 🙏 출처

- KOBIS 박스오피스 | KMDB 영화데이터 | 카카오맵 지도
- 공공데이터포털의 영화상영관 
