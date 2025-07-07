# Oops-back

### 📁 프로젝트 구조
```
  Oops-backend/ 
  ├── src/ 
  │ └── main/ 
  │     ├── java/
  │     │    └── Oops/
  │     │        └── backend/
  │     │            ├── auth/
  │     │            ├── common/
  │     │            ├── config/
  │     │            ├── domain/ 
  │     │            └── ... 
  │     └── resources/ 
  │         ├── application.yml 
  │         ├── application-dev.yml 
  │         ├── application-local.yml 
```

---
## 🔧 프로젝트 설정

- **프로젝트명**: Oops
- **Spring Boot 버전**: 3.5.3
- **Java 버전**: 17

## 📁 Branch Strategy

### 🔀 Git Flow

| 브랜치 이름               | 용도                                                          |
| -------------------- | ----------------------------------------------------------- |
| `main`               | 프로덕션 배포용 브랜치                                                |
| `develop`                | 개발 환경 브랜치                                                   |
| `feature/#이슈번호-기능이름` | 기능 개발 브랜치 <br>예: `feature/#12-email`, `feature/#23-payment` |

### ✅ 브랜치 운영 규칙

* 모든 기능 개발은 `develop` 브랜치에서 분기합니다.
* 작업 완료 후 `develop` 브랜치로 Pull Request(PR)을 생성합니다.
* 코드 리뷰 후 `develop`에 머지합니다.
* 주기적으로 `develop` → `main`으로 머지하여 배포합니다.

---

## 📦 PR Convention

### ✅ PR 템플릿

```md
## 📋 작업 내용
- 구현한 기능에 대한 상세 설명

## 🎯 관련 이슈
- closes #이슈번호

## 📝 변경 사항
- [ ] 기능 A 구현
- [ ] 기능 B 수정
- [ ] 테스트 코드 작성

## 📸 스크린샷 (선택사항)
- 필요한 경우 스크린샷 첨부

## ✅ 체크리스트
- [ ] 코드 컨벤션 준수
- [ ] 주석 작성
- [ ] 문서 업데이트
- [ ] 리뷰어 지정
```

---

## 🧾 Issue Convention

### 🛠 기능 이슈 템플릿

```md
## 📌 기능 설명
> 구현하고자 하는 기능을 상세히 설명해주세요.

## 📋 작업 내용
- [ ] 작업 1
- [ ] 작업 2
- [ ] 작업 3

## 📎 참고사항
- 관련 문서:
- 참고 링크:
```

### 🐛 오류 이슈 템플릿

```md
## 🐛 버그 설명
> 발생한 버그에 대해 설명해주세요.

## 재현 방법
1. 
2. 
3. 

## 예상 동작
> 정상적으로 동작해야 하는 내용

## 실제 동작
> 현재 발생하는 문제

## 📸 스크린샷
> 오류 메시지나 화면 캡처
```

---

## 💬 Commit Convention

### 🧱 커밋 메시지 형식

```
이모지 타입: 제목
본문 (선택사항)
꼬리말 (선택사항)
```

### 🔖 타입별 규칙

| 이모지 | 타입         | 설명                                |
| --- | ---------- | --------------------------------- |
| ✨   | `feat`     | 새로운 기능 추가                         |
| 🐛  | `fix`      | 버그 수정                             |
| 📚  | `docs`     | 문서 수정                             |
| 💄  | `style`    | 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없음) |
| ♻️  | `refactor` | 코드 리팩토링                           |
| 🧪  | `test`     | 테스트 코드 작성 또는 수정                   |
| 🚀  | `deploy`   | 배포 관련 설정 변경                       |
| 🧹  | `chore`    | 빌드, 패키지 매니저 설정 등 기타 변경            |

---
## 📦 기본 응답 통일 코드

- `BaseResponse<T>`: 모든 API 응답을 통일된 형식(`code`, `message`, `data`)으로 감싸 반환합니다.
- 응답 상태 관리는 `SuccessStatus`, `ErrorStatus` Enum 클래스를 통해 일관되게 처리합니다.

---

## ⚠️ 공통 예외 처리 코드

- `ExceptionAdvice`: `@RestControllerAdvice`를 이용한 전역 예외 처리 클래스입니다.
- `GeneralException` 및 다양한 커스텀 예외 클래스를 통해 예외를 계층적으로 관리합니다.
- 일관된 에러 포맷(`BaseResponse`)으로 클라이언트에 응답을 전달합니다.

---

## 📘 Swagger 설정 코드

- `SwaggerConfig`를 통해 Swagger UI를 설정하였습니다.
- 개발 중 API 명세는 다음 주소에서 확인할 수 있습니다:
  - [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🔐 인증 처리 설정 코드

- `Spring Security`와 `JWT(Json Web Token)`를 기반으로 인증을 구현하였습니다.
- 핵심 구성 요소:
  - `JwtUtil`: 토큰 생성 및 검증 유틸리티
  - `JwtAuthenticationFilter`: JWT 필터링을 위한 커스텀 필터
  - `SecurityConfig`: 인가 및 보안 설정 구성
