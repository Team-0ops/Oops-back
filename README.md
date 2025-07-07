# Oops-back
## 📠 Git Convention

###프로젝트 구조
Oops-backend/
├── src/
│   └── main/
│       ├── java/Oops/backend/
│       │   ├── auth/
│       │   ├── common/
│       │   ├── config/
│       │   ├── domain/
│       │   └── ...
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-local.yml



---

### Commit Convention

| Prefix   | Description                                     |
|----------|-------------------------------------------------|
| feat     | 새로운 기능 구현                                  |
| fix      | 버그 수정, 예외 처리 등 기능 개선                    |
| refactor | 기능 변화 없이 코드 구조 개선                        |
| setting  | 패키지 설치, 환경 설정 등 개발 환경 세팅               |
| docs     | 문서 작성 및 수정 (README, 주석 등)                |
| chore    | 기타 작업 (빌드, 테스트 코드 수정 등)               |

#### 커밋 작성 규칙

- 형식: `<type>: <작업 내용>`
- 예시:
  - `feat: 회원가입 API 구현`
  - `fix: 실패담 작성 예외 처리`
  - `refactor: 리뷰 점수 검증 로직 분리`
- 여러 작업을 동시에 한 경우, **핵심 작업을 먼저** 쓰고, 한 줄에 하나씩 작성
