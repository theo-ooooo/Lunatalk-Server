# 🛍️ Lunatalk Server

**Lunatalk**는 Laravel로 운영되던 쇼핑몰 백엔드를 Java 21과 Spring Boot 3.4.x 기반으로 전면 재구축한 프로젝트입니다.  
JWT 기반 인증, 결제 연동, AWS 인프라, CI/CD 자동화 등 실제 서비스 수준의 기능을 포함하고 있으며, 기능 단위로 모듈화된 레이어드 아키텍처 구조로 구성되어 있습니다.

---

## 🚀 주요 기능

- JWT 기반 회원 인증 및 인가
- 상품 등록 및 이미지 업로드 (AWS S3)
- 장바구니 및 주문 처리
- 배송지 관리 및 상태 추적
- 토스페이먼츠 결제 연동
- 관리자 상품/주문 관리 기능

---

## ⚙️ 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.x, Spring Security, Spring Data JPA |
| Database | MySQL 8.x, Redis |
| Infra | AWS EC2, RDS, S3, Docker, GitHub Actions |
| Auth | JWT (Access / Refresh) |
| Docs | Swagger / OpenAPI |
| CI/CD | GitHub Actions → EC2 배포 자동화

---

## 🗂️ 패키지 구조

```bash
src/main/java/kr/co/lunatalk
├── LunatalkServerApplication.java
├── domain
│   ├── auth
│   ├── category
│   ├── common
│   ├── delivery
│   ├── image
│   ├── member
│   ├── order
│   ├── payment
│   └── product
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
        ├── properties
        ├── redis
        └── s3
```

> 각 도메인은 controller, domain, dto, repository, service로 구성되어 있으며,  
> 기능 중심 모듈화와 계층적 책임 분리를 통해 유지보수가 용이한 구조입니다.

---

## 🧪 테스트 및 문서화

- **JUnit 5 기반 단위 및 슬라이스 테스트 구성**
- **명시적 의존성 주입 방식 테스트 설계**
- **Swagger UI API 문서 제공**  
  → [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

---

## 🛠️ 개발자 노트

> "기존 Laravel 시스템을 Spring Boot 3.4.x + Java 21 기반으로 전면 재구축하며,  
> 실제 서비스 배포를 염두에 둔 구조로 구현했습니다.  
> 결제 시스템, AWS 기반 인프라 구성, CI/CD 자동화, 캐시 최적화 등 실무에서 필요한 기술을 적용하며  
> 코드 품질과 확장성을 모두 고려했습니다."

---

## 🔗 GitHub Migration Repository

🔗 https://github.com/seojindev/lunatalk.backend

---
