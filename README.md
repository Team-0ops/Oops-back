# Oops-back

---
## 🔧 프로젝트 설정

- **프로젝트명**: 실패를 공유하는 새로운 SNS, Oops!
- **주요 기술 스택**:
  - Java 17
  - Spring Boot 3
  - JWT + 쿠키 기반 인증
  - MySQL (RDS 연동)
  - AWS S3 (이미지 업로드)
  - Swagger (API 문서화)
- **배포 환경**:
  - AWS EC2 (백엔드 서버)
  - AWS RDS (MySQL)
  - GitHub Actions (CI/CD)

---

## 👥 팀원 소개

| <center>김다영<br/>[@kdyann](https://github.com/kdyann)</center> | <center>백가현<br/>[@GahBaek](https://github.com/GahBaek)</center> | <center>홍진기<br/>[@llokr1](https://github.com/llokr1)</center> | <center>김혜린<br/>[@dodaaaaam](https://github.com/dodaaaaam)</center> |
| :-------------------------------------------------------------: | :--------------------------------------------------------------: | :--------------------------------------------------------------: | :----------------------------------------------------------------: |
| <img src="https://avatars.githubusercontent.com/u/143780983?v=4" width="150px"/> | <img src="https://avatars.githubusercontent.com/u/127074448?v=4" width="150px"/> | <img src="https://avatars.githubusercontent.com/u/191385250?v=4" width="150px"/> | <img src="https://avatars.githubusercontent.com/u/199376763?v=4" width="150px"/> |

---

### 📁 프로젝트 구조
```
  Oops-backend/
  ├── .github/
  │       └──ISSUE_TEMPLATE/
  │       └──workflows/
  │             └──oops-deploy.yml
  │       └──pull_request_template.md
  ├── src/ 
  │ └── main/ 
  │     ├── java/
  │     │    └── Oops/
  │     │        └── backend/
  │     │            ├── common/
  │     │            ├── config/  
  │     │            ├── domain/
  │     │            │     └── auth/
  │     │            │           └── controller/
  │     │            │           └── dto/
  │     │            │           └── entity/
  │     │            │           └── repository/
  │     │            │           └── service/
  │     │            └── ... 
  │     └── resources/ 
  │         ├── application.yml 
  │         ├── application-dev.yml 
  │         ├── application-local.yml
  │         ├── application-security.yml 
  │         ├── application-s3.yml
  │         ├── application-openai.yml  
```

---
### 📁 서버 아키텍처
<img width="621" height="410" alt="웁스 서버 아키텍처" src="https://github.com/user-attachments/assets/2a719125-6cf2-4a13-a6d5-40746da953d0" />


---


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
## 📦 기본 응답 통일 

- 모든 API는 BaseResponse<T> 형식으로 응답됩니다.

- 성공/실패 응답은 SuccessStatus, ErrorStatus enum으로 일관성 있게 관리합니다.
```md
return new BaseResponse(true, "COMMON200", "SUCCESS!", null, data);
```
---

## ⚠️ 공통 예외 처리 

- @RestControllerAdvice를 활용해 모든 예외를 통합 처리합니다.

- 커스텀 예외는 GeneralException으로 정의하며, 응답은 BaseResponse 포맷 유지

```md
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<BaseResponse> handleValidation(MethodArgumentNotValidException e) {
    return BaseResponse.onFailure(ErrorStatus.VALIDATION_ERROR, e.getMessage());
}
```

---

## 📘 Swagger 설정 
- Swagger(OpenAPI 3.0) 설정을 통해 API 명세를 확인할 수 있습니다.

- 접속 주소: http://15.164.217.202:8080/swagger-ui/index.html

```md
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Oops API")
                .description("실패 경험 공유 플랫폼")
                .version("1.0.0"));
    }
}
```

---

## 🔐 인증 처리 설정 (Spring Security + JWT)
직접 구현한 **쿠키 기반 JWT 인증 처리**

**주요 구성**
- `JwtEncoder`, `JwtTokenProvider`: JWT 생성 및 디코딩
- `AuthenticationInterceptor`: 요청 전 쿠키에서 토큰 추출 및 사용자 인증
- `AuthenticationContext`: ThreadLocal 기반 인증 유저 저장소
- `@AuthenticatedUser`: 인증된 사용자 주입용 커스텀 어노테이션
- `AuthenticatedUserArgumentResolver`: 컨트롤러에 인증 유저 바인딩

---

