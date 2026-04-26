# OpenBootCamp 개발 계획 (plan.md)

> spec.md를 기반으로 작성된 단계별 개발 계획입니다.
> 원칙: **가장 단순한 것부터 시작 → 점진적으로 기능 추가**
> 각 Phase는 독립적으로 동작하는 상태로 완료되어야 합니다.

---

## 기술 스택 요약

### Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 25 | 언어 |
| Spring Boot | 4.0.x | 웹 프레임워크 |
| Spring Security | (Boot 내장) | 인증/인가 |
| Spring Data JPA | (Boot 내장) | ORM |
| Spring Scheduler | (Boot 내장) | 배치/스케줄링 |
| H2 | (Boot 내장) | 개발용 인메모리 DB |
| PostgreSQL | 16.x | 프로덕션 DB |
| Gradle | 8.x | 빌드 도구 |
| JWT (jjwt) | 0.12.x | 토큰 발급/검증 |
| GitHub REST API v3 | - | PR/코드리뷰 수집 |

### Frontend
| 기술 | 버전 | 용도 |
|------|------|------|
| Next.js | 15.x (App Router) | 프레임워크 |
| TypeScript | 5.x | 언어 |
| Tailwind CSS | 4.x | 스타일링 |
| shadcn/ui | latest | UI 컴포넌트 |
| Zustand | 5.x | 전역 상태 관리 |
| TanStack Query | 5.x | 서버 상태 / 캐싱 |
| next-auth | 5.x (Auth.js) | GitHub OAuth 클라이언트 |

### 공통 / 인프라
| 기술 | 용도 |
|------|------|
| Vercel | 프론트엔드 배포 |
| Railway 또는 AWS EC2 | 백엔드 배포 |
| GitHub Actions | CI/CD |

---

## 개발 원칙

1. **Phase별 독립 동작**: 각 Phase가 끝나면 해당 기능을 실제로 브라우저에서 확인할 수 있어야 함
2. **백엔드 API 우선**: 각 Phase에서 BE API를 먼저 만들고, FE가 이를 소비하는 순서로 진행
3. **H2로 빠르게 개발**: 개발 환경은 H2 인메모리 DB 사용, PostgreSQL 전환은 Phase 4에서
4. **테스트 동반**: 각 Phase에서 핵심 로직(서비스 레이어)은 단위 테스트 작성
5. **커밋 단위**: 기능 단위로 커밋 (feat/fix/refactor 컨벤션)

---

## Phase 0 — 프로젝트 초기 설정

> **목표**: 백엔드·프론트엔드 모두 Hello World 수준으로 실행되는 상태

### 0-1. 백엔드 초기 설정

**디렉토리**: `backend/`

```
tasks:
- [x] Spring Initializr로 프로젝트 생성
      - Java 25, Gradle, Spring Boot 4.0.x
      - 의존성: Web, JPA, H2, Lombok, Validation
- [x] application.yml 기본 설정
      - H2 콘솔 활성화 (/h2-console)
      - JPA ddl-auto: create-drop (개발)
      - 로깅 레벨 설정
- [x] 기본 보안 정책 원칙 설정 (주석으로 명시)
      // 기본값: 모든 GET 요청은 비로그인 허용 (permitAll)
      // 쓰기(POST/PUT/DELETE)는 기본 인증 필요
      // 역할별 세분화는 Phase 2 SecurityConfig에서 완성
      // ※ CORS 전역 설정은 Phase 2에서 Spring Security 의존성 추가 시 SecurityConfig에서 함께 설정
- [x] 공통 응답 형식 클래스 작성
      - ApiResponse<T> { success, data, message, errorCode }
- [x] 전역 예외 핸들러 (GlobalExceptionHandler)
- [x] Health Check 엔드포인트 GET /api/v1/health
- [x] 서버 실행 확인 (포트 8080)
```

**application.yml 구조**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:openbootcamp
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

app:
  jwt:
    secret: ${JWT_SECRET:local-dev-secret-key}
    access-token-expiry: 900000    # 15분
    refresh-token-expiry: 604800000 # 7일
  github:
    client-id: ${GITHUB_CLIENT_ID}
    client-secret: ${GITHUB_CLIENT_SECRET}
    token: ${GITHUB_TOKEN:}        # Rate Limit용 PAT
```

---

### 0-2. 프론트엔드 초기 설정

**디렉토리**: `frontend/`

```
tasks:
- [x] create-next-app 생성 (TypeScript, App Router, Tailwind)
- [x] shadcn/ui 초기화 — lib/utils.ts (cn 헬퍼) 수동 설정, 컴포넌트는 Phase 1부터 추가
- [x] TanStack Query Provider 설정 (providers/QueryProvider.tsx)
- [x] Zustand 스토어 기본 구조 (store/authStore.ts)
- [x] API 클라이언트 설정 (lib/api.ts — fetch wrapper, baseURL)
- [x] 환경변수 설정 (.env.local)
      - NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
- [x] 공통 레이아웃 (components/layout/Header.tsx, Footer.tsx)
- [x] 메인 페이지 "/" 기본 화면
- [x] 실행 확인 (포트 3000)
```

**패키지 설치**:
```bash
npm install @tanstack/react-query zustand axios
npm install next-auth@beta
npx shadcn@latest add button card badge input label
```

---

## Phase 1 — 부트캠프 CRUD

> **목표**: 부트캠프를 등록·조회·수정·삭제할 수 있는 가장 기본적인 기능

### 1-1. 백엔드

**엔티티 설계**:
```java
// Enum: TrackType, OperationType, TechStack (spec.md 7-2 참고)

// Bootcamp.java
@Entity @Table(name = "bootcamps")
public class Bootcamp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;          // unique
    private String slug;          // unique, URL-friendly
    private String logoUrl;
    @Column(length = 500)
    private String description;
    private String officialUrl;

    // operationType, priceMin/Max, durationWeeks는 트랙마다 다르므로 BootcampTrack으로 이동
    @OneToMany(mappedBy = "bootcamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BootcampTrack> tracks = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// BootcampTrack.java
@Entity @Table(name = "bootcamp_tracks")
public class BootcampTrack {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bootcamp_id", nullable = false)
    private Bootcamp bootcamp;

    @Enumerated(EnumType.STRING)
    private TrackType trackType;      // BACKEND, FRONTEND, FULLSTACK, MOBILE, ...

    @Enumerated(EnumType.STRING)
    private OperationType operationType;  // ONLINE, OFFLINE, HYBRID

    @ElementCollection
    @CollectionTable(name = "bootcamp_track_tech_stacks",
                     joinColumns = @JoinColumn(name = "bootcamp_track_id"))
    @Column(name = "tech_stack")
    @Enumerated(EnumType.STRING)
    private List<TechStack> techStacks = new ArrayList<>();

    private Integer priceMin;         // 만원 단위
    private Integer priceMax;
    private Integer durationWeeks;
    private Boolean isRecruiting;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

```
# Domain 레이어
- [x] Enum 작성: TrackType, OperationType, TechStack, ProjectStatus, Role
- [x] Bootcamp 엔티티 (@EnableJpaAuditing, @CreatedDate, @LastModifiedDate)
- [x] BootcampTrack 엔티티 (@ManyToOne Bootcamp)
- [x] Domain 단위 테스트 (BootcampTest, BootcampTrackTest)

# Repository 레이어
- [x] BootcampRepository, BootcampTrackRepository
- [x] Repository 단위 테스트 (BootcampRepositoryTest — 13케이스, BootcampTrackRepositoryTest — 2케이스)

# Service 레이어
- [x] 슬러그 자동 생성 유틸 (SlugUtils — kebab-case, 중복 시 suffix)
- [x] BootcampDto / BootcampTrackDto (Request/Response — Java Record)
- [x] BootcampService (부트캠프 + 트랙 함께 등록/수정/삭제, Specification 필터링)
- [x] Service 단위 테스트 (BootcampServiceTest — 17케이스)

# Controller 레이어
- [x] BootcampController
      - GET  /api/v1/bootcamps               (목록, 페이징)
      - GET  /api/v1/bootcamps/{id}          (ID로 단건 조회 — tracks 포함)
      - GET  /api/v1/bootcamps/slug/{slug}   (slug로 단건 조회 — tracks 포함)
      - POST /api/v1/bootcamps               (등록, 인증 없이 허용 — Phase 2에서 권한 추가)
      - PUT  /api/v1/bootcamps/{id}          (수정)
      - DELETE /api/v1/bootcamps/{id}        (삭제)
      - POST /api/v1/bootcamps/{id}/tracks       (트랙 추가)
      - PUT  /api/v1/bootcamps/{id}/tracks/{trackId}  (트랙 수정)
      - DELETE /api/v1/bootcamps/{id}/tracks/{trackId} (트랙 삭제)
- [x] Controller 통합 테스트 (BootcampControllerTest — 16케이스)
```

**쿼리 파라미터** (`GET /bootcamps`):
```
?trackType=BACKEND&operationType=ONLINE&techStack=SPRING_BOOT&keyword=위코드&sort=latest&page=0&size=20
```

---

### 1-2. 프론트엔드

```
tasks:
- [x] Bootcamp 타입 정의 (lib/types.ts)
- [x] bootcamp API 함수 (lib/bootcampApi.ts)
- [x] 부트캠프 탐색 페이지 /bootcamps
      - 카드 그리드 (BootcampCard 컴포넌트)
      - 기본 페이지네이션
- [x] 부트캠프 상세 페이지 /bootcamps/[id]
      - 기본 정보 표시
- [x] 부트캠프 등록 페이지 /bootcamps/new (임시 — 인증 없이)
      - 폼 (이름, 설명, 운영형태, 트랙)

# Slug 기반 라우팅 전환
- [x] 백엔드가 제공하는 slug 필드를 활용하여 URL 구조 변경
      - /bootcamps/[id] → /bootcamps/[slug] 로 라우트 변경
      - BootcampCard의 링크를 slug 기반으로 변경 (href={`/bootcamps/${bootcamp.slug}`})
      - bootcampApi.ts에 getBootcampBySlug(slug) 함수 추가
        (GET /api/v1/bootcamps/slug/{slug} 백엔드 엔드포인트 활용)
      - 부트캠프 등록/수정 완료 후 리다이렉트를 slug 기반으로 변경

# 부트캠프 수정/삭제
- [x] 부트캠프 수정 페이지 /bootcamps/[slug]/edit
      - 기존 데이터 prefill (서버 컴포넌트에서 slug로 조회 후 클라이언트 폼에 전달)
      - [기본 정보 저장] 버튼 → PUT /api/v1/bootcamps/{id} 호출 → 성공 시 상세 페이지로 이동
- [x] 부트캠프 삭제 기능
      - 상세 페이지에 [삭제] 버튼 배치 (DeleteBootcampButton 클라이언트 컴포넌트)
      - 삭제 확인 모달 다이얼로그
      - DELETE /api/v1/bootcamps/{id} 호출 → 성공 시 /bootcamps로 이동
- [x] 부트캠프 상세 페이지에 [수정] / [삭제] 버튼 추가 (임시 — 인증 없이 표시)

# 트랙 관리 UI
- [x] 부트캠프 수정 페이지에 트랙 관리 섹션 추가
      - 현재 등록된 트랙 목록 표시 (trackType, operationType, techStacks, 가격, 기간, 모집여부)
      - [트랙 추가] 버튼 → 인라인 폼 열기
        입력 필드: trackType(선택), operationType(선택), techStacks(다중 선택),
                   priceMin, priceMax, durationWeeks, isRecruiting(체크박스)
        → POST /api/v1/bootcamps/{id}/tracks 호출
      - 트랙 항목마다 [수정] 버튼
        → 해당 트랙 값으로 인라인 폼 초기화 → 수정 후 PUT /api/v1/bootcamps/{id}/tracks/{trackId}
      - 트랙 항목마다 [삭제] 버튼
        → DELETE /api/v1/bootcamps/{id}/tracks/{trackId} → 로컬 상태 즉시 반영
      - 트랙 추가/수정/삭제 완료 후 트랙 목록 자동 갱신 (로컬 state 업데이트)
- [x] 부트캠프 상세 페이지의 트랙 목록에 트랙 정보 상세 표시
      - 각 트랙: trackType 배지, operationType 배지, techStacks 태그, 가격 범위, 기간, 모집 상태
```

**완료 기준**: 브라우저에서 부트캠프 등록(slug 자동생성 확인) → 목록 조회(slug 기반 링크) → 상세 조회(트랙 상세 표시) → 수정(트랙 추가/수정/삭제 포함) → 삭제 가능

---

## Phase 2 — 회원가입 / 로그인

> **목표**: GitHub OAuth 로그인, JWT 발급, 역할 기반 접근 제어

### 2-1. 백엔드

**엔티티**:
```java
// User.java — AbstractEntity 상속 (id, createdAt, updatedAt, active)
// githubId는 User에 직접 두지 않고 UserOAuth로 분리 (다중 OAuth 제공자 지원)
@Entity @Table(name = "users")
public class User extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String username;
    private String email;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // STUDENT, BOOTCAMP_ADMIN, ADMIN
}

// OAuthProvider.java
public enum OAuthProvider {
    GITHUB("github"), KAKAO("kakao"), GOOGLE("google");
    // from(registrationId): 제공자 ID 문자열 → enum 변환
}

// UserOAuth.java — OAuth 제공자별 연동 정보 (provider + providerId 복합 유니크)
@Entity @Table(name = "user_oauth")
public class UserOAuth extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;
    private String providerId;  // OAuth 제공자 내 사용자 고유 ID
}

// RefreshToken.java — refreshToken DB 저장 (로그아웃 시 무효화, 재사용 방지)
@Entity @Table(name = "refresh_tokens")
public class RefreshToken extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false, unique = true)
    private String token;
    private LocalDateTime expiresAt;
    // invalidate(): 토큰 만료 처리
}
```

```
tasks:
- [x] User 엔티티 + Role Enum
- [x] OAuthProvider Enum + UserOAuth 엔티티 (OAuth 제공자 분리)
- [x] UserRepository, UserOAuthRepository
- [x] GitHub OAuth2 설정
      - application-oauth.yml에 client-id, client-secret 설정
      - OAuth2 콜백 경로: /login/oauth2/code/github (Spring Security 자동 처리)
      - CustomOAuth2UserService, OAuth2UserInfo 계층 (GithubOAuth2UserInfo)
      - OAuth2AuthenticationSuccessHandler: upsert → JWT 발급 → 쿠키 저장 → FE 리다이렉트
- [x] JWT 유틸 클래스 (JwtProvider — 발급, 검증, Claims 추출)
- [x] CookieProvider (HttpOnly 쿠키 설정/추출/삭제)
- [x] JwtAuthenticationFilter (요청마다 쿠키에서 AT 검증)
- [x] AuthService (인터페이스 + DefaultAuthService — upsertFromOAuth2, findById, saveRefreshToken, refresh, logout)
- [x] RefreshToken 엔티티 + RefreshTokenRepository (DB 저장으로 로그아웃 시 무효화)
      - 도메인 단위 테스트 (RefreshTokenTest — 9케이스)
      - Repository 테스트 (RefreshTokenRepositoryTest — 5케이스)
      - OAuth2AuthenticationSuccessHandler에서 saveRefreshToken() 호출 추가 완료
- [x] AuthController
      - POST /api/v1/auth/refresh  AT 재발급 (RT 쿠키 검증 → DB 조회 → 새 AT 발급)
      - POST /api/v1/auth/logout   RT 무효화 + 쿠키 삭제
      - GET  /api/v1/auth/me   [AUTH]
      - Controller 통합 테스트 (AuthControllerTest — 6케이스)
- [x] SecurityConfig — CORS 전역 설정 + 경로별 접근 권한 설정 (구현 완료)
- [x] Phase 1 API에 권한 추가
      - POST/PUT /bootcamps → BOOTCAMP_ADMIN or ADMIN
      - DELETE /bootcamps → ADMIN only
- [x] 단위 테스트 (JwtProviderTest, JwtAuthenticationFilterTest)
- [x] 단위 테스트 (AuthServiceTest — 13케이스)
```

---

### 2-2. 프론트엔드

```
tasks:
- [ ] next-auth (Auth.js v5) 설정
      - GitHub Provider
      - JWT 세션 전략
- [ ] 로그인 페이지 /login
      - "GitHub으로 로그인" 버튼
- [ ] Header에 로그인/로그아웃 상태 표시
- [ ] 인증 필요 페이지 보호 (middleware.ts)
- [ ] AuthStore (Zustand) — 로그인 상태 전역 관리
- [ ] API 클라이언트에 Authorization 헤더 자동 주입
```

**OAuth 흐름**:
```
브라우저 → [GitHub 로그인 버튼] → next-auth → GitHub OAuth
→ BE /auth/github/callback → User upsert → JWT 발급
→ next-auth 세션에 JWT 저장 → 이후 API 요청에 Bearer 포함
```

**완료 기준**: GitHub 로그인 후 `/auth/me` 응답으로 본인 정보 확인, 비로그인 시 등록 버튼 비활성화

---

## Phase 3 — 프로젝트 등록 및 GitHub 연동 (4~5일)

> **목표**: 수강생이 GitHub 레포를 등록하면 메타데이터를 자동 수집하여 쇼케이스에 노출

### 3-1. 백엔드

**엔티티**:
```java
// Project.java
@Entity @Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Bootcamp bootcamp;
    private String repoUrl;
    private String repoOwner;
    private String repoName;
    private String title;
    @Column(length = 1000)
    private String description;

    // GitHub topics → TechStack Enum 매핑 (매핑 안 되는 topic은 language 필드에 자유 문자열로 보관)
    @ElementCollection
    @CollectionTable(name = "project_tech_stacks",
                     joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech_stack")
    @Enumerated(EnumType.STRING)
    private List<TechStack> techStacks = new ArrayList<>();

    private String domainTag;
    private String language;        // GitHub 주요 언어 (자유 문자열)
    private String thumbnailUrl;
    private Integer stars;
    private Integer forks;
    private String cohort;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;   // DRAFT, PUBLISHED, ARCHIVED
    private LocalDateTime syncedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**GitHub API 연동**:
```
GitHub REST API 호출 대상:
- GET /repos/{owner}/{repo}          → 레포 기본 정보
- GET /repos/{owner}/{repo}/topics   → 토픽(기술태그)
- GET /repos/{owner}/{repo}/readme   → README (Base64 디코딩)
```

```
tasks:
- [ ] Project 엔티티 + ProjectStatus Enum
- [ ] ProjectRepository
- [ ] GitHubApiClient (RestClient 또는 RestTemplate 래퍼)
      - GitHub PAT 헤더 자동 주입
      - Rate Limit 처리 (429 시 대기)
- [ ] GitHubRepoMetadata DTO
- [ ] ProjectService
      - registerProject(repoUrl, userId, bootcampId, ...) → GitHub API 호출 → Project 저장
      - syncProject(projectId) → 최신 데이터로 갱신
- [ ] ProjectController
      - GET  /api/v1/projects               (목록, 필터/페이징)
      - GET  /api/v1/projects/{id}          (상세)
      - POST /api/v1/projects     [AUTH]    (등록)
      - PUT  /api/v1/projects/{id} [AUTH]   (수정)
      - DELETE /api/v1/projects/{id} [AUTH] (삭제)
      - POST /api/v1/projects/{id}/sync [AUTH] (수동 재동기화)
- [ ] 단위 테스트 (GitHubApiClientTest — MockServer 사용)
```

---

### 3-2. 프론트엔드

```
tasks:
- [ ] Project 타입 정의
- [ ] 프로젝트 등록 페이지 /projects/new
      - GitHub URL 입력 → "불러오기" 클릭 → 미리보기 표시
      - 부트캠프 선택, 기수, 팀/개인, 태그 입력
      - [공개 등록] 버튼
- [ ] 프로젝트 쇼케이스 /projects
      - 필터 (부트캠프, 기술 스택, 도메인)
      - 카드 그리드 (ProjectCard 컴포넌트)
- [ ] 프로젝트 상세 /projects/[id]
      - GitHub 링크, 기술 태그, 설명
      - 기본 통계 (stars, forks)
```

**완료 기준**: GitHub URL 입력 후 레포 정보 자동 표시, 등록 완료 시 쇼케이스에 노출

---

## Phase 4 — 코드리뷰 수집 및 열람

> **목표**: 등록된 프로젝트의 PR과 코드리뷰를 GitHub에서 수집하여 열람 가능

### 4-1. 백엔드

**엔티티**:
```java
// PullRequest.java
@Entity @Table(name = "pull_requests")
public class PullRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;
    private Integer prNumber;
    private String title;
    @Column(length = 5000)
    private String body;
    private String state;           // open, closed, merged
    private String authorGithubLogin;
    private LocalDateTime prCreatedAt;
}

// CodeReview.java
@Entity @Table(name = "code_reviews")
public class CodeReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private PullRequest pullRequest;
    private Long githubReviewId;
    private String reviewerLogin;
    private String state;           // APPROVED, CHANGES_REQUESTED, COMMENTED
    @Column(length = 10000)
    private String body;
    private Integer commentCount;
    private LocalDateTime submittedAt;
}
```

**GitHub API 수집 대상**:
```
GET /repos/{owner}/{repo}/pulls?state=all&per_page=100
GET /repos/{owner}/{repo}/pulls/{pull_number}/reviews
GET /repos/{owner}/{repo}/pulls/{pull_number}/comments
```

```
tasks:
- [ ] PullRequest, CodeReview 엔티티
- [ ] PullRequestRepository, CodeReviewRepository
- [ ] GitHubApiClient 확장 (PR, Review 수집 메서드)
- [ ] CodeReviewSyncService
      - syncAllReviews(projectId) → PR 목록 수집 → 각 PR의 리뷰 수집 → 저장
- [ ] Spring Scheduler 설정
      - @Scheduled(cron = "0 0 3 * * *")  매일 새벽 3시 전체 갱신
      - syncAllPublishedProjects()
- [ ] API 추가
      - GET /api/v1/projects/{id}/pull-requests
      - GET /api/v1/projects/{id}/pull-requests/{prNumber}/reviews
- [ ] 단위 테스트 (CodeReviewSyncServiceTest)
```

---

### 4-2. 프론트엔드

```
tasks:
- [ ] 프로젝트 상세 페이지에 PR 목록 추가
      - PR 카드 (제목, 상태, 리뷰어, 코멘트 수)
      - [코드리뷰 보기] 링크
- [ ] 코드리뷰 상세 /projects/[id]/pr/[prNumber]
      - PR 본문
      - 리뷰어별 코멘트 목록
      - 상태 배지 (APPROVED / CHANGES_REQUESTED / COMMENTED)
- [ ] 부트캠프 상세 페이지에 코드리뷰 탭 추가
      - 해당 부트캠프의 전체 코드리뷰 수 집계 표시
```

**완료 기준**: 프로젝트 상세에서 PR 목록 확인, 코드리뷰 내용 열람 가능

---

## Phase 5 — 부트캠프 비교 + 리뷰

> **목표**: 부트캠프 간 데이터 비교 및 수강 경험 리뷰 작성

### 5-1. 백엔드

**엔티티**:
```java
// BootcampReview.java
@Entity
@Table(name = "bootcamp_reviews",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "bootcamp_id"}))
public class BootcampReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Bootcamp bootcamp;
    @Min(1) @Max(5)
    private Integer rating;
    @Column(length = 500)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

```
tasks:
- [ ] BootcampReview 엔티티
- [ ] BootcampReviewRepository
- [ ] BootcampReviewService (등록/수정/삭제 + 평균 별점 계산)
- [ ] BootcampStatsService
      - getStats(bootcampId): 프로젝트 수, 코드리뷰 수, 평균 별점, 기술 스택 분포
- [ ] API 추가
      - GET  /api/v1/bootcamps/{id}/reviews
      - POST /api/v1/bootcamps/{id}/reviews  [AUTH]
      - PUT  /api/v1/reviews/{id}  [AUTH]
      - DELETE /api/v1/reviews/{id}  [AUTH]
      - GET  /api/v1/bootcamps/{id}/stats
      - GET  /api/v1/bootcamps/compare?ids=1,2,3
```

---

### 5-2. 프론트엔드

```
tasks:
- [ ] 부트캠프 상세 페이지에 리뷰 탭 추가
      - 별점 입력 + 텍스트 리뷰 폼
      - 리뷰 목록 표시
- [ ] 부트캠프 비교 페이지 /compare
      - 부트캠프 검색 후 추가 (최대 3개)
      - 비교 테이블 (수강료, 기간, 프로젝트 수, 리뷰 수, 평균 별점)
      - 기술 스택 분포 (레이더 차트 — recharts 사용)
- [ ] 부트캠프 카드에 별점 표시 업데이트
- [ ] 메인 홈에 통계 카운터 추가 (부트캠프 N개, 프로젝트 N개)
```

**패키지 추가**:
```bash
npm install recharts
```

**완료 기준**: 3개 부트캠프 비교 화면 동작, 리뷰 등록/조회 가능

---

## Phase 6 — 마이페이지 + 운영사 대시보드

> **목표**: 수강생 마이페이지, 운영사 전용 대시보드

### 6-1. 백엔드

```
tasks:
- [ ] UserService
      - getMyProjects(userId)
      - getMyReviews(userId)
- [ ] BootcampAdminService
      - getBootcampDashboard(bootcampId): 조회수, 리뷰 변화, 프로젝트 목록
- [ ] 역할 검증 강화
      - @PreAuthorize("hasRole('BOOTCAMP_ADMIN')")
- [ ] API 추가
      - GET /api/v1/users/me/projects   [AUTH]
      - GET /api/v1/users/me/reviews    [AUTH]
      - GET /api/v1/admin/bootcamp/dashboard  [BOOTCAMP_ADMIN]
```

---

### 6-2. 프론트엔드

```
tasks:
- [ ] 마이페이지 /my
      - 내 프로젝트 목록 (수정/삭제 버튼)
      - 내가 남긴 리뷰 목록
      - GitHub 연동 상태
- [ ] 운영사 대시보드 /admin/bootcamp
      - 자사 프로젝트 목록
      - 누적 통계 카드 (프로젝트 수, 코드리뷰 수, 평균 별점)
      - 최근 등록 프로젝트
- [ ] 역할별 메뉴 분기 (Header)
```

**완료 기준**: 마이페이지에서 본인 프로젝트 관리, 운영사 계정으로 로그인 시 대시보드 접근 가능

---

## Phase 7 — 품질 개선 + 프로덕션 준비

> **목표**: PostgreSQL 전환, 배포, 성능 최적화

### 7-1. DB 전환 (H2 → PostgreSQL)

```
tasks:
- [ ] application-prod.yml 작성
      - datasource: PostgreSQL URL/user/password (환경변수)
      - ddl-auto: validate (Flyway로 마이그레이션 관리)
- [ ] Flyway 의존성 추가 + 초기 마이그레이션 스크립트 작성
      - V1__init_schema.sql
- [ ] 인덱스 추가
      - bootcamps(slug), projects(repo_owner, repo_name), pull_requests(project_id, pr_number)
- [ ] 쿼리 최적화 (N+1 문제 → fetch join 또는 @EntityGraph)
```

---

### 7-2. 성능 및 UX

```
tasks:
- [ ] Next.js Image 최적화 (next/image 적용)
- [ ] 무한 스크롤 또는 페이지네이션 UX 개선
- [ ] Skeleton Loading 컴포넌트
- [ ] 에러 바운더리 / 에러 페이지 (error.tsx, not-found.tsx)
- [ ] SEO 메타태그 (generateMetadata)
- [ ] GitHub API Rate Limit 모니터링 로그
```

**완료 기준**: 프로덕션 URL에서 모든 Phase 기능이 동작

---

## Phase 8 — Phase 2 확장 기능 (선택적, 우선순위 순)

> **목표**: 사용자 경험을 높이는 부가 기능

### 우선순위 순

| 순위 | 기능 | 설명 |
|------|------|------|
| 1 | 프로젝트 좋아요 / 북마크 | 관심 프로젝트 저장, 마이페이지에서 확인 |
| 2 | 부트캠프 공식 Verified 배지 | 운영사 승인 후 배지 부여 |
| 3 | 멘토 프로필 | 코드리뷰 횟수 많은 멘토 노출 |
| 4 | Private 레포 연동 | GitHub App 권한 위임으로 Private PR 수집 |
| 5 | 추천 알고리즘 | 열람 이력 기반 추천 |


---

## 후순위 / 리팩토링 작업

> 각 Phase의 핵심 구현이 완료된 이후에 진행하는 작업들입니다.
> 상단 항목일수록 먼저 진행하고, 하단으로 갈수록 우선순위가 낮습니다.

---

### [Phase 2 완료 후] 전체 소프트 삭제 전환

> **목표**: PHASE 2 백엔드 작업 완료 후, 전 도메인 하드 삭제 → 소프트 삭제 전환
>
> **배경**: `AbstractEntity`에 `active` 필드(기본값 true)를 이미 추가해 두었으나,
> `Bootcamp` / `BootcampTrack` 등 기존 엔티티의 삭제 로직이 아직 하드 삭제(`deleteById`)로 동작 중.

```
tasks:
- [ ] BootcampService.deleteBootcamp — deleteById() → bootcamp.deactivate() + flush
- [ ] BootcampService.deleteTrack    — deleteById() → track.deactivate() + flush
- [ ] BootcampRepository 조회 쿼리 전체에 WHERE active = true 조건 추가
      - findById, findBySlug, findByFilters(페이징) 등
- [ ] BootcampTrackRepository 조회 쿼리에 WHERE active = true 조건 추가
- [ ] 이후 추가되는 모든 도메인(Project, BootcampReview 등)은
      처음부터 소프트 삭제 방식으로 구현
- [ ] 서비스 테스트: deleteBootcamp 후 existsById → false 대신
      findById 결과의 active = false 로 검증 방식 변경
- [ ] 컨트롤러 테스트: 삭제 후 GET 요청 시 404 반환 검증 유지
      (서비스에서 active = false 항목을 not found 처리)
```

**주의사항**:
- 현재 `BootcampServiceTest.deleteBootcamp_success`는 `existsById == false`로 검증 중 → 소프트 삭제 전환 시 검증 로직 수정 필요
- 쿼리 필터 누락 시 비활성 데이터가 목록에 노출되는 버그 발생 가능 — 전환 시 전체 쿼리 점검 필수

---

### [최후순위] Spring Boot 4 API Versioning 적용

> **목표**: Spring Boot 4에서 공식 지원하는 API Versioning 기능을 적용하여 `/api/v1/` 경로 관리를 프레임워크 수준으로 위임
>
> **배경**: 현재는 `@RequestMapping("/api/v1/...")` 방식으로 경로에 버전을 직접 하드코딩 중.
> Spring Boot 4에서 `ApiVersionRequestMappingHandlerMapping`이 추가되어 어노테이션 기반 버전 관리가 가능해짐.

```
tasks:
- [ ] Spring Boot 4 API Versioning 설정 추가
      - application.yml에 spring.mvc.pathmatch.api-version 설정
- [ ] 전체 Controller에서 @RequestMapping 경로의 /api/v1 접두사 제거
      - @ApiVersion("1") 어노테이션으로 대체
- [ ] SecurityConfig 경로 패턴도 버전 설정에 맞게 수정
- [ ] Controller 통합 테스트 경로 확인 및 수정
```

**참고**:
- 현재 모든 Controller가 `/api/v1/...` 패턴을 직접 명시하므로 변경 범위가 전체 도메인에 걸침
- 모든 Phase 완료 후 일괄 적용 권장

---

> **이 파일 활용법**:
> Claude Code에 작업을 요청할 때 해당 Phase와 섹션을 명시하면 일관된 코드 생성이 가능합니다.
> 예시: `"plan.md Phase 3-1과 spec.md의 F-03을 참고하여 ProjectService.java를 작성해주세요."`
