# moviesite — 오늘의 영화

> 실시간 박스오피스와 영화 정보를 한눈에 확인할 수 있는 영화 정보 통합 서비스

---

## 📌 프로젝트 소개

**KOBIS(영화진흥위원회)**와 **KMDB(한국영화데이터베이스)** API를 통합하여  
실시간 박스오피스 순위와 영화 상세 정보를 제공하는 웹 서비스입니다.

### 주요 특징
- 🎬 **일일 박스오피스** - KOBIS API를 통한 실시간 순위 제공
- 📽️ **영화 상세 정보** - KMDB API 연동으로 포스터, 줄거리, 출연진 정보 제공
- 🏢 **전국 극장 정보** - 공공데이터포털의 영화상영관 표준데이터 활용
- 🔄 **자동 데이터 동기화** - Spring @Scheduled를 통한 일일 자동 갱신

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
| **External APIs** | KOBIS API, KMDB API, Kakao Map API |

---

## 🚀 실행 방법

### 1. 레포지토리 클론
```bash
git clone https://github.com/hanlee99/moviesite.git
cd moviesite/demo
```

### 2. 환경변수 설정

#### Windows
```bash
setx KOBIS_API_KEY "발급받은_KOBIS_API키"
setx KMDB_API_KEY "발급받은_KMDB_API키"
```

#### macOS / Linux
```bash
export KOBIS_API_KEY="발급받은_KOBIS_API키"
export KMDB_API_KEY="발급받은_KMDB_API키"
```

또는 `application.properties`에서 직접 설정:
```properties
api.kobis.key=발급받은_KOBIS_API키
api.kmdb.key=발급받은_KMDB_API키
```

### 3. 애플리케이션 실행
```bash
# Gradle 사용
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar
```

### 4. 접속
```
http://localhost:8080
```

---

## ✨ 주요 기능

### 1. 일일 박스오피스
- KOBIS API를 통한 실시간 박스오피스 순위 제공
- Spring Cache 적용으로 불필요한 API 호출 최소화 (1일 캐싱)
- 순위, 관객수, 매출액 정보 표시

### 2. 영화 정보 통합
- KMDB API 연동으로 영화 포스터 자동 수집
- 감독, 배우, 장르, 줄거리 등 상세 정보 제공
- DB 우선 검색 → KMDB API Fallback 전략

### 3. 현재 상영작 / 개봉 예정작
- 페이징 처리로 대용량 데이터 효율적 조회
- 개봉일 기준 자동 분류

### 4. 전국 극장 정보
- 공공데이터포털의 500+ 극장 정보 제공
- 브랜드별, 지역별 필터링 지원

### 5. 자동 데이터 동기화
- Spring @Scheduled를 통한 일일 박스오피스 자동 갱신
- 중복 체크 및 증분 업데이트 로직

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
│   │   │   ├── mapper/             # Entity ↔ DTO 변환(kmdb내부dto를 영화와 영화로 변환)
│   │   │   ├── exception/          # 예외 처리
│   │   │   ├── config/             # Spring 설정
│   │   │   └── scheduler/          # 배치 작업
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
| GET | `/movie/box-office/daily` | 일일 박스오피스 조회 | - |
| GET | `/movie/now-playing` | 현재 상영작 목록 | page, size |
| GET | `/movie/upcoming` | 개봉 예정작 목록 | page, size |
| GET | `/movie/search/db` | DB 내부 영화 검색 | title |
| GET | `/` | 메인 페이지 (Thymeleaf) | - |

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

---

## 🔧 트러블슈팅

### 1. API 응답 지연 개선
**문제**: KMDB API 호출 시 3초 이상 소요  
**해결**: 영화 기본 정보를 DB에 캐싱, 필요 시에만 API 호출  
**결과**: 평균 응답 속도 50% 개선

### 2. 외부 API 장애 대응
**문제**: KOBIS API 일시적 장애 시 서비스 중단  
**해결**: 예외 처리 + DB에 저장된 최근 데이터 제공 (Fallback)  
**결과**: 서비스 가용성 향상

---

## 📝 향후 개선 계획

- [ ] 테스트 커버리지 80% 달성
- [ ] API 문서 자동화 (Swagger/SpringDoc)
- [ ] Redis 캐싱 레이어 구현
- [ ] Docker 컨테이너화 및 배포 자동화
- [ ] 위치 기반 극장 검색 기능 완성 (좌표 변환 EPSG5174)

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
