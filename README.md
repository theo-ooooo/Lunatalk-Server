# 🛍️ Lunatalk Server

**Lunatalk**는 Laravel로 운영되던 쇼핑몰 백엔드를 Kotlin과 Spring Boot 4 기반으로 전면 재구축한 프로젝트입니다.
JWT 기반 인증, 카카오 OAuth 소셜 로그인, 결제 연동, AWS 인프라, CI/CD 자동화 등 실제 서비스 수준의 기능을 포함하고 있으며, 기능 단위로 모듈화된 레이어드 아키텍처 구조로 구성되어 있습니다.

---

## 🚀 주요 기능

- JWT 기반 회원 인증 및 인가
- 카카오 OAuth 소셜 로그인
- 상품 등록 및 이미지 업로드 (AWS S3)
- 장바구니 및 주문 처리
- 배송지 관리 및 상태 추적
- 토스페이먼츠 결제 연동
- 결제 통계 집계 (스케줄러)
- 관리자 대시보드 API
- 기획전 관리
- 상품 좋아요 기능
- 1:1 문의 및 답변

---

## ⚙️ 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Kotlin 2.3 |
| Framework | Spring Boot 4.0, Spring Framework 7, Spring Security, Spring Data JPA |
| Build | Gradle 9.4 (Kotlin DSL) |
| Database | MySQL 8.x, Redis |
| ORM | JPA / Hibernate, QueryDSL 5 |
| Infra | AWS ECS, ECR, RDS, S3, Docker, GitHub Actions |
| Auth | JWT (Access / Refresh), Kakao OAuth |
| Docs | Swagger / springdoc-openapi 3.0 |
| CI/CD | GitHub Actions → ECR → ECS 배포 자동화 |
| Test | JUnit 5, Mockito-Kotlin |

---

## 🗂️ 패키지 구조

```bash
src/main/kotlin/kr/co/lunatalk
├── LunatalkServerApplication.kt
├── domain
│   ├── auth
│   ├── cartitem
│   ├── category
│   ├── common
│   ├── dashboard
│   ├── delivery
│   ├── exhibition
│   ├── image
│   ├── inquiry
│   ├── member
│   ├── order
│   ├── payment
│   ├── paymentstatistics
│   ├── product
│   └── productlike
├── global
│   ├── common
│   ├── config
│   ├── exception
│   ├── filter
│   ├── jpa
│   ├── security
│   └── util
└── infra
    └── config
        ├── jwt
        ├── mail
        ├── properties
        ├── redis
        ├── s3
        └── toss
```

> 각 도메인은 controller, domain, dto, repository, service로 구성되어 있으며,
> 기능 중심 모듈화와 계층적 책임 분리를 통해 유지보수가 용이한 구조입니다.

---

## 🧪 테스트 및 문서화

- **JUnit 5 + Mockito-Kotlin 기반 단위 테스트**
- **Spring Boot 통합 테스트 구성**
- **Swagger UI API 문서 제공**
  → [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

---

## 🛠️ 개발자 노트

> "기존 Laravel 시스템을 Spring Boot 4 + Kotlin 기반으로 전면 재구축하며,
> 실제 서비스 배포를 염두에 둔 구조로 구현했습니다.
> 결제 시스템, AWS 기반 인프라 구성, CI/CD 자동화, 캐시 최적화 등 실무에서 필요한 기술을 적용하며
> 코드 품질과 확장성을 모두 고려했습니다."

---

## 🔗 GitHub Migration Repository

🔗 https://github.com/seojindev/lunatalk.backend

---
