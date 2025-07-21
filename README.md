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

- 접속 주소: http://localhost:8080/swagger-ui/index.html

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
- 로그인 시 JWT 발급, 이후 요청 시 Authorization: Bearer {token} 헤더 사용

- 인증 필터에서 토큰 검증 및 사용자 인증 처리
```md
// 로그인 예시
@PostMapping("/login")
public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    String token = jwtUtil.generateToken(1L); // 예시
    return ResponseEntity.ok(token);
}
```

```md
// JWT 필터 예시
if (jwtUtil.validateToken(token)) {
    Long userId = jwtUtil.extractUserId(token);
    SecurityContextHolder.getContext().setAuthentication(...);
}
```
