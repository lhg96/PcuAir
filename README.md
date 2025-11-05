# PcuAir - 공기질 모니터링 API 서버

IoT 센서로부터 공기질 데이터를 수집하고 관리하는 RESTful API 서버입니다.

## 기술 스택
- Google App Engine (Java 8)
- Google Cloud Datastore
- Maven, Gson

## API 엔드포인트

### 공기질 데이터 (`/api/air`)
**측정 항목**: PM1.0, PM2.5, PM10.0, VOC, CO, Radon, 온도, 습도

- `POST /api/air` - 데이터 생성
- `GET /api/air?command=list&did={디바이스ID}` - 목록 조회
- `GET /api/air?command=last&did={디바이스ID}` - 최신 데이터 조회
- `DELETE /api/air?command=delete&aid={공기질ID}` - 데이터 삭제

### 디바이스 관리 (`/api/device`)
- `POST /api/device` - 디바이스 등록
- `GET /api/device?command=list` - 디바이스 목록
- `PUT /api/device?command=update&did={디바이스ID}` - 정보 수정
- `DELETE /api/device?command=delete&did={디바이스ID}` - 디바이스 삭제

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



## 프로젝트 구조
```
src/main/java/pcu/
├── api/           # REST API 서블릿
├── dao/           # 데이터 접근 계층  
├── vo/            # 데이터 모델
└── util/          # 유틸리티
```
```
src/
├── main/
│   ├── java/
│   │   └── pcu/
│   │       ├── api/                         # API 서블릿
│   │       │   ├── AirServlet.java          # 공기질 데이터 API
│   │       │   ├── DeviceServlet.java       # 디바이스 관리 API
│   │       │   └── NTPTime.java             # 시간 동기화
│   │       ├── dao/                         # 데이터 접근 계층
│   │       │   ├── AirDAO.java              # 공기질 DAO 인터페이스
│   │       │   ├── AirDAOImpl.java          # 공기질 DAO 구현
│   │       │   ├── DeviceDAO.java           # 디바이스 DAO 인터페이스
│   │       │   └── DeviceDAOImpl.java       # 디바이스 DAO 구현
│   │       ├── util/                        # 유틸리티
│   │       │   ├── CustomUtil.java          # 커스텀 유틸리티
│   │       │   ├── ShardedCounter.java      # 분할 카운터
│   │       │   └── ShardedCounterServlet.java
│   │       └── vo/                          # 값 객체
│   │           ├── Air.java                 # 공기질 데이터 모델
│   │           ├── Device.java              # 디바이스 모델
│   │           └── Result.java              # 결과 래퍼 모델
│   └── webapp/
│       ├── index.html                       # 메인 페이지
│       └── WEB-INF/
│           ├── appengine-web.xml            # App Engine 설정
│           ├── web.xml                      # 웹 애플리케이션 설정
│           └── datastore-indexes.xml        # Datastore 인덱스 설정
└── test/                                    # (선택) 단위 테스트 디렉터리
```

## 실행 방법
```bash
# 로컬 실행
mvn appengine:run

# 배포
mvn appengine:deploy
```
