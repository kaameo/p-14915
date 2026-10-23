# FAQ AI Chatbot

Spring AI와 PGroonga 기반의 FAQ 검색 챗봇입니다. 사용자의 질문을 받으면 AI가 FAQ 검색 도구를 호출하고, PostgreSQL에 저장된 질문과 답변을 검색하여 한국어 답변을 스트리밍합니다.

## 주요 기능

- Spring WebFlux 기반 AI 응답 스트리밍
- Spring AI Tool Calling을 이용한 FAQ 검색
- PGroonga 기반 한국어 전문 검색
- R2DBC를 이용한 비동기 데이터 접근
- 애플리케이션 시작 시 스키마 및 기본 FAQ 데이터 자동 생성
- OpenAI 기반 기본 실행 및 Ollama 기반 테스트 환경

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 4.1.1, Spring WebFlux |
| AI | Spring AI 2.0.1, OpenAI, Ollama |
| Database | PostgreSQL 18, PGroonga |
| Data Access | Spring Data R2DBC |
| Test | JUnit 5, WebTestClient, Testcontainers |
| Build | Gradle 9.8.2 |

## 동작 구조

```text
Client
  └─ GET /api/v1/ai/chat?message=...
       └─ ChatService
            ├─ ChatClient
            └─ FaqService (AI Tool)
                 └─ FaqRepository
                      └─ PostgreSQL + PGroonga
```

1. 클라이언트가 질문을 전송합니다.
2. AI가 질문에 필요한 FAQ 검색 키워드를 판단합니다.
3. `FaqService.searchFaq()` 도구가 PGroonga로 질문과 답변을 검색합니다.
4. AI가 검색 결과를 바탕으로 한국어 답변을 스트리밍합니다.

## 실행 전 준비

- Java 25
- Docker 및 Docker Compose
- OpenAI API Key

## 실행 방법

### 1. 저장소 실행 준비

프로젝트 루트에 `.env` 파일을 생성합니다.

```dotenv
OPENAI_API_KEY=your-openai-api-key
DATABASE_NAME=faqdb
DATABASE_USERNAME=faq
DATABASE_PASSWORD=faq123
```

데이터베이스 관련 값은 생략하면 위 기본값을 사용합니다. `OPENAI_API_KEY`는 기본 프로필 실행에 반드시 필요합니다.

### 2. 인프라 실행

```bash
docker compose up -d postgres
```

PostgreSQL은 `localhost:5432`에서 실행됩니다. 최초 애플리케이션 실행 시 `faq` 테이블, PGroonga 확장, 기본 FAQ 데이터가 자동으로 생성됩니다.

### 3. 애플리케이션 실행

macOS/Linux:

```bash
./gradlew bootRun
```

Windows:

```bat
gradlew.bat bootRun
```

기본 서버 주소는 `http://localhost:8080`입니다.

## API 사용법

### FAQ 챗봇

```http
GET /api/v1/ai/chat?message={질문}
```

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Query Parameter | `message` (필수) |
| Response | 스트리밍 텍스트 |

호출 예시:

```bash
curl -N -G "http://localhost:8080/api/v1/ai/chat" \
  --data-urlencode "message=환불 방법을 알려주세요"
```

질문 예시:

- `배송은 얼마나 걸리나요?`
- `비밀번호를 잊어버렸어요.`
- `포인트 적립률을 알려주세요.`
- `교환은 어떻게 신청하나요?`

## 테스트

테스트 프로필은 로컬 Ollama와 Testcontainers 기반 PGroonga를 사용합니다. 테스트 실행 전 Docker를 실행하고 Ollama 모델을 준비합니다.

```bash
docker compose up -d ollama
docker compose exec ollama ollama pull qwen3.6:35b
./gradlew test
```

테스트 항목은 다음과 같습니다.

- FAQ 전문 검색
- PGroonga 키워드 검색
- FAQ 저장 및 전체 조회
- AI 응답 스트리밍 API

## 프로젝트 구조

```text
src
├── main
│   ├── java/com/back
│   │   ├── ai
│   │   │   ├── controller/AiController.java
│   │   │   └── service/ChatService.java
│   │   ├── faq
│   │   │   ├── entity/Faq.java
│   │   │   ├── repository/FaqRepository.java
│   │   │   └── service/FaqService.java
│   │   └── global
│   │       ├── config/AiConfig.java
│   │       └── initData/BaseInitData.java
│   └── resources
│       ├── application.yaml
│       ├── application-test.yaml
│       └── schema/faq-schema.sql
└── test/java/com/back
    └── BackApplicationTests.java
```

## 환경 변수

| 변수 | 필수 여부 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `OPENAI_API_KEY` | 필수 | 없음 | 기본 프로필에서 사용할 OpenAI API Key |
| `DATABASE_NAME` | 선택 | `faqdb` | PostgreSQL 데이터베이스 이름 |
| `DATABASE_USERNAME` | 선택 | `faq` | PostgreSQL 사용자 이름 |
| `DATABASE_PASSWORD` | 선택 | `faq123` | PostgreSQL 비밀번호 |

## 종료

컨테이너를 종료합니다.

```bash
docker compose down
```

데이터 볼륨까지 삭제하려면 다음 명령을 사용합니다. 이 경우 저장된 PostgreSQL 및 Ollama 데이터가 모두 삭제됩니다.

```bash
docker compose down -v
```
