# PcuAir - 공기질 모니터링 API 서버

IoT 센서로부터 수집한 공기질 데이터를 저장·조회하고 측정 장치를 관리하는 RESTful API 서버입니다. Google App Engine Standard 환경에서 실행되며, Swagger UI를 통해 API 문서를 제공합니다.

## 주요 기능
- 공기질 측정치 생성/조회/삭제
- 디바이스 등록, 수정, 삭제 및 상세 조회
- 커서 기반 목록 페이징
- App Engine Datastore 및 Sharded Counter 연동
- Swagger UI 기반의 대화형 API 문서 제공

## 기술 스택
- Java 8 (Servlet)
- Maven
- Google App Engine Standard / Datastore
- Gson

## API 엔드포인트

### 공기질 데이터 (`/api/air`)
**측정 항목**: PM1.0, PM2.5, PM10.0, VOC, CO, Radon, 온도, 습도

- `POST /api/air` – 측정 데이터 생성
- `GET /api/air?command=list&did={디바이스ID}` – 디바이스별 데이터 목록
- `GET /api/air?command=last&did={디바이스ID}` – 최신 측정 데이터
- `GET /api/air?command=read&aid={데이터ID}` – 단일 데이터 조회
- `DELETE /api/air?command=delete&aid={데이터ID}` – 데이터 삭제
- `DELETE /api/air?command=deleteall&did={디바이스ID}` – 디바이스 전체 데이터 삭제

### 디바이스 관리 (`/api/device`)
- `POST /api/device` – 디바이스 등록
- `GET /api/device?command=list` – 디바이스 목록
- `GET /api/device?command=read&did={디바이스ID}` – 디바이스 상세
- `PUT /api/device?command=update&did={디바이스ID}` – 디바이스 정보 수정
- `DELETE /api/device?command=delete&did={디바이스ID}` – 디바이스 삭제

### 기타 (`/api/time`)
- `GET /api/time` – 서버 기준 현재 시간 반환

## 사용 예제

### 공기질 데이터 전송
```bash
curl -X POST "http://localhost:8080/api/air" \
  -d "did=a101&dust25=35&voc=150&temp=23.5&hum=65"
```

### 디바이스 등록
```bash
curl -X POST "http://localhost:8080/api/device" \
  -d "did=a101&title=강의실1&email=admin@pcu.ac.kr&latitude=36.35&longitude=127.38"
```

## Swagger 문서
- 로컬 서버 실행 후 `http://localhost:8080/swagger/`에서 Swagger UI 확인
- OpenAPI 명세 파일: `src/main/webapp/swagger/swagger.json`
- 명세 수정 시 Swagger UI 새로고침만으로 즉시 반영

## 데이터 모델 개요

| 엔티티 | 주요 필드 | 설명 |
| --- | --- | --- |
| `Air` | `did`, `aid`, `dust10`, `dust25`, `dust100`, `voc`, `co`, `radon`, `hum`, `temp`, `regDate` | 공기질 측정 데이터 |
| `Device` | `did`, `title`, `description`, `email`, `latitude`, `longitude`, `authKey`, `regDate`, `lastUpdate`, `lastAir` | 측정 장치 정보 |

## 프로젝트 구조

```
src/
├── main/
│   ├── java/pcu/
│   │   ├── api/        # 서블릿 API (Air, Device, NTPTime)
│   │   ├── dao/        # Datastore DAO
│   │   ├── util/       # Sharded Counter 등 유틸리티
│   │   └── vo/         # 값 객체 (Air, Device, Result)
│   └── webapp/
│       ├── swagger/    # Swagger UI 및 OpenAPI 스펙
│       └── WEB-INF/    # web.xml, appengine-web.xml 등 설정
└── test/               # (선택) 단위 테스트
```

## 실행 방법
```bash
# 로컬 실행
mvn appengine:run

# 배포
mvn appengine:deploy
```
