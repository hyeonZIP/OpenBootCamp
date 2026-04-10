# OpenBootCamp Spec (Product Requirements Document)

> **SDD(Spec-Driven Development) 최적화 버전**
> Claude Code가 이 문서를 컨텍스트로 참조하여 코드를 생성할 수 있도록 구조화되었습니다.
> 최종 수정: 2026-04-10

---

## 목차

1. [Executive Summary](#1-executive-summary)
2. [Problem Statement](#2-problem-statement)
3. [User Personas](#3-user-personas)
4. [User Stories](#4-user-stories)
5. [Feature Specifications](#5-feature-specifications)
6. [System Architecture Overview](#6-system-architecture-overview)
7. [Data Model](#7-data-model)
8. [API Endpoints](#8-api-endpoints)
9. [UI/UX Requirements](#9-uiux-requirements)

---

## 1. Executive Summary

### 한 줄 정의
> **OpenBootCamp**는 부트캠프 수강생의 프로젝트·멘토링 품질을 외부에 투명하게 공개하여, IT 취준생이 데이터 기반으로 부트캠프를 선택할 수 있도록 돕는 투명성 플랫폼이다.

### 핵심 가치 제안 (Value Proposition)

| 대상 | 기존 문제 | OpenBootCamp 가치 |
|------|-----------|-------------------|
| IT 취준생 | 유명세·마케팅만 보고 부트캠프 선택 | 실제 수강생 프로젝트·코드리뷰로 품질 판단 |
| 부트캠프 수강생 | 본인 프로젝트를 알릴 채널 없음 | 프로젝트 쇼케이스로 포트폴리오 홍보 |
| 부트캠프 운영사 | 교육 품질을 증명할 객관적 수단 없음 | 데이터 기반 신뢰도 구축 및 공식 채널 운영 |

### 성공 지표 (KPI)
- MAU 5,000명 달성 (런칭 6개월 내)
- 등록 부트캠프 20개 이상
- 등록 프로젝트 500개 이상
- 멘토링/코드리뷰 열람 평균 세션 시간 3분 이상

---

## 2. Problem Statement

### 2-1. 정량적 문제

- 국내 IT 부트캠프 시장 규모는 2024년 기준 약 **3,000억원** 이상으로 추정 (연 20%+ 성장)
- 주요 부트캠프 수는 **100개 이상**으로 급증했으나, 객관적 품질 비교 수단 전무
- 취준생의 부트캠프 선택 기준 설문(비공개 자료 추정):
  - 지인 추천: ~40%
  - 브랜드 인지도: ~35%
  - 실제 커리큘럼/품질: ~15%
  - 기타: ~10%
- 부트캠프 수료 후 취업률 공시 의무 없음 → 자체 발표 수치의 신뢰도 낮음

### 2-2. 정성적 문제

1. **정보 비대칭**: 부트캠프 수강생의 실력·프로젝트 결과물이 외부에 전혀 공개되지 않음
2. **멘토링 블랙박스**: 코드리뷰·멘토링 품질이 내부 채널(Slack, Github Private Repo)에만 존재
3. **마케팅 과잉**: 화려한 광고와 취업률 통계 외에 실질적 교육 품질을 판단할 지표 없음
4. **수강생 포트폴리오 격차**: 동일 부트캠프 수강 후에도 프로젝트 노출 기회가 개인 역량에 따라 극단적으로 갈림

### 2-3. 기회 (Opportunity)

- Github API를 통해 공개된 코드리뷰·커밋 데이터를 집계·시각화 가능
- 수강생의 자발적 프로젝트 공개 욕구(포트폴리오) + 취준생의 정보 수요가 동시 존재
- "부트캠프 투명성"이라는 포지셔닝은 현재 경쟁 공백 상태

---

## 3. User Personas

### Persona A — 박지수 (IT 취준생, 25세)

```
직업: 비전공 학원 수료 후 취준 중
목표: 6개월 내 백엔드 개발자 취업
불편함:
  - 부트캠프 홈페이지는 전부 자화자찬처럼 느껴짐
  - 수강 경험담 블로그는 오래되거나 광고성이 많음
  - 실제 수강생이 만든 프로젝트 수준을 보고 싶음
기대:
  - 내가 원하는 기술 스택(Spring, React)을 가르치는 곳의 실제 프로젝트를 보고 싶다
  - 멘토가 어떤 식으로 코드리뷰를 해주는지 확인하고 싶다
  - 여러 부트캠프를 나란히 비교하고 싶다
```

### Persona B — 김민준 (부트캠프 수강생, 23세)

```
직업: A 부트캠프 수강 중 (백엔드 트랙)
목표: 포트폴리오를 공개하여 취업 시장에서 유리한 고지 점령
불편함:
  - GitHub에 올려도 아무도 안 봄
  - 내가 다니는 부트캠프가 좋은 곳인데 외부에서 잘 모름
  - 프로젝트 소개할 창구가 없음
기대:
  - 내 프로젝트와 코드리뷰 내역을 플랫폼에 올려 자동으로 노출되길 원함
  - 같은 부트캠프 동료들과 함께 쇼케이스 페이지에 소개되길 원함
  - 좋아요·댓글 등 반응을 통해 프로젝트 가시성을 높이고 싶음
```

### Persona C — 이현우 (부트캠프 운영사 담당자, 35세)

```
직업: B 부트캠프 마케팅/운영팀장
목표: 자사 부트캠프의 교육 품질을 객관적으로 증명하고 수강생 모집
불편함:
  - 취업률 외에 교육 품질을 증명할 수단이 없음
  - SNS 광고 비용은 증가하는데 전환율은 낮아짐
  - 우수 수강생 프로젝트를 공신력 있게 노출할 채널이 없음
기대:
  - 공식 채널 페이지를 통해 자사 커리큘럼·수강생 성과를 공개하고 싶음
  - 수강생 프로젝트가 외부 플랫폼에 자동으로 집계·노출되길 원함
  - 경쟁 부트캠프 대비 객관적 지표(프로젝트 수, 코드리뷰 횟수 등)로 비교되길 원함
```

---

## 4. User Stories

### Persona A (취준생) — 박지수

> **[비로그인 가능]**: 회원가입·로그인 없이 접근 가능
> **[로그인 필요]**: GitHub OAuth 로그인 후 이용 가능

```
US-A01: 부트캠프 탐색                                  [비로그인 가능]
As a 취준생,
I want to 기술 스택(Java, React 등)과 가격대로 부트캠프를 필터링하고 싶다,
So that 내 목표에 맞는 부트캠프 후보를 빠르게 추릴 수 있다.

US-A02: 프로젝트 열람                                  [비로그인 가능]
As a 취준생,
I want to 특정 부트캠프 수강생들이 만든 프로젝트 목록을 볼 수 있다,
So that 해당 부트캠프에서 배울 수 있는 실제 결과물 수준을 파악할 수 있다.

US-A03: 코드리뷰 열람                                  [비로그인 가능]
As a 취준생,
I want to 부트캠프 멘토가 남긴 코드리뷰 내용을 열람할 수 있다,
So that 멘토링의 깊이와 품질을 직접 확인하고 선택에 반영할 수 있다.

US-A04: 부트캠프 비교                                  [비로그인 가능]
As a 취준생,
I want to 최대 3개 부트캠프를 나란히 비교(프로젝트 수, 코드리뷰 수, 평점)할 수 있다,
So that 최종 선택을 데이터 기반으로 내릴 수 있다.

US-A05: 부트캠프 리뷰 열람                             [비로그인 가능]
As a 취준생,
I want to 다른 수강생이 남긴 부트캠프 별점·리뷰를 열람하고 싶다,
So that 실제 수강 경험을 간접적으로 파악하고 선택에 참고할 수 있다.

US-A06: 평가 남기기                                    [로그인 필요]
As a 취준생(또는 수강 경험자),
I want to 열람한 부트캠프에 별점과 리뷰를 남길 수 있다,
So that 같은 고민을 가진 다른 사람들에게 도움을 줄 수 있다.
```

### Persona B (수강생) — 김민준

```
US-B01: 프로젝트 등록
As a 부트캠프 수강생,
I want to GitHub 레포지토리 URL을 입력해 내 프로젝트를 플랫폼에 등록하고 싶다,
So that 내 프로젝트가 플랫폼에 공개되어 외부에서 볼 수 있게 된다.

US-B02: GitHub 연동
As a 부트캠프 수강생,
I want to GitHub OAuth로 로그인하고 내 레포지토리 정보를 자동으로 가져오고 싶다,
So that 수동 입력 없이 프로젝트를 빠르게 등록할 수 있다.

US-B03: 코드리뷰 자동 집계
As a 부트캠프 수강생,
I want to 내 레포지토리의 PR 코드리뷰가 자동으로 플랫폼에 표시되길 원한다,
So that 별도 작업 없이 멘토링 기록이 공개된다.

US-B04: 프로젝트 홍보
As a 부트캠프 수강생,
I want to 내 프로젝트에 태그(기술 스택, 도메인)를 붙이고 소개글을 작성하고 싶다,
So that 검색 시 노출 가능성을 높이고 인상적인 소개를 할 수 있다.
```

### Persona C (운영사) — 이현우

```
US-C01: 부트캠프 공식 채널 등록
As a 부트캠프 운영사,
I want to 자사 부트캠프 공식 페이지를 플랫폼에 등록하고 싶다,
So that 잠재 수강생들이 공식 정보를 확인하고 자사를 선택할 수 있다.

US-C02: 수강생 프로젝트 연결
As a 부트캠프 운영사,
I want to 우리 부트캠프 수강생의 프로젝트가 자사 페이지에 자동으로 연결되길 원한다,
So that 교육 성과를 집계된 형태로 보여줄 수 있다.

US-C03: 통계 대시보드
As a 부트캠프 운영사,
I want to 자사 수강생 프로젝트 수, 코드리뷰 횟수, 평균 별점 등 통계를 확인하고 싶다,
So that 교육 품질 개선 및 마케팅 자료로 활용할 수 있다.
```

---

## 5. Feature Specifications

### 5-0. 접근 권한 매트릭스 (Access Control Matrix)

> OpenBootCamp는 **투명성 플랫폼**이므로, 핵심 조회 기능은 모두 비로그인 사용자에게 개방한다.
> 쓰기·개인화 기능만 로그인을 요구한다.

| 기능 | VISITOR (비로그인) | STUDENT | BOOTCAMP_ADMIN | ADMIN |
|------|:-----------------:|:-------:|:--------------:|:-----:|
| 메인 홈 / 통계 조회 | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 목록 탐색 / 필터 | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 상세 조회 | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 트랙 정보 조회 | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 통계 조회 | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 비교 (최대 3개) | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 리뷰 열람 | ✅ | ✅ | ✅ | ✅ |
| 프로젝트 목록 / 상세 조회 | ✅ | ✅ | ✅ | ✅ |
| PR 목록 조회 | ✅ | ✅ | ✅ | ✅ |
| 코드리뷰 열람 | ✅ | ✅ | ✅ | ✅ |
| 부트캠프 등록 / 수정 | ❌ | ❌ | ✅ | ✅ |
| 부트캠프 삭제 | ❌ | ❌ | ❌ | ✅ |
| 프로젝트 등록 / 수정 / 삭제 | ❌ | ✅ (본인) | ❌ | ✅ |
| GitHub 데이터 수동 재동기화 | ❌ | ✅ (본인) | ❌ | ✅ |
| 부트캠프 리뷰 작성 / 수정 / 삭제 | ❌ | ✅ (본인) | ❌ | ✅ |
| 프로젝트 좋아요 / 북마크 | ❌ | ✅ | ✅ | ✅ |
| 마이페이지 | ❌ | ✅ | ✅ | ✅ |
| 운영사 대시보드 | ❌ | ❌ | ✅ (본인 캠프) | ✅ |
| 관리자 기능 | ❌ | ❌ | ❌ | ✅ |

**비로그인(VISITOR) 가능 범위 요약**:
- 모든 **조회(GET)** 기능: 부트캠프, 트랙, 프로젝트, PR, 코드리뷰, 리뷰, 통계, 비교
- 불가: 데이터 생성·수정·삭제, 마이페이지, 좋아요/북마크

---

### 5-1. MVP (Phase 1) — 핵심 기능

#### F-01. 회원가입 / 로그인

| 항목 | 내용 |
|------|------|
| 인증 방식 | GitHub OAuth 2.0 (필수), 이메일+비밀번호 (선택) |
| 역할 | `VISITOR`(비로그인), `STUDENT`(수강생), `BOOTCAMP_ADMIN`(운영사), `ADMIN`(플랫폼 관리자) |
| JWT | Access Token(15분) + Refresh Token(7일), HttpOnly Cookie |
| 소셜 연동 | GitHub 계정 연결 시 GitHub username, avatar, public repos 수집 |
| 비로그인 처리 | JWT 없는 요청은 VISITOR로 간주, 공개 GET 엔드포인트는 인증 없이 200 응답 |

#### F-02. 부트캠프 등록 및 탐색

| 항목 | 내용 |
|------|------|
| 등록 주체 | `BOOTCAMP_ADMIN` 또는 `ADMIN` |
| 필수 필드 | 이름, 로고, 한 줄 소개, 공식 URL, 운영 형태(온라인/오프라인/혼합), 트랙(백엔드/프론트/풀스택/etc.) |
| 선택 필드 | 수강료 범위, 기간(주), 모집 인원, 취업 지원 여부 |
| 탐색 | 기술 스택 태그 필터, 운영 형태 필터, 트랙 필터, 키워드 검색 |
| 정렬 | 최신순, 프로젝트 수 순, 평점 순 |

#### F-03. 프로젝트 등록 및 쇼케이스

| 항목 | 내용 |
|------|------|
| 등록 주체 | `STUDENT` 역할 보유 사용자 |
| 등록 방식 | GitHub 레포지토리 URL 입력 → GitHub REST API로 메타데이터 자동 수집 |
| 수집 데이터 | repo name, description, stars, forks, language, topics, last push, README(마크다운) |
| 필수 입력 | 소속 부트캠프 선택, 수강 기수, 팀/개인 구분, 대표 이미지(선택) |
| 태그 | 기술 스택(자유 입력 + 자동완성), 도메인(커머스/소셜/헬스케어/etc.) |
| 상태 | `DRAFT` → `PUBLISHED` → `ARCHIVED` |

#### F-04. GitHub 코드리뷰 열람 (GitHub REST API)

| 항목 | 내용 |
|------|------|
| 데이터 소스 | GitHub REST API v3 (`/repos/{owner}/{repo}/pulls`, `/pulls/{pull_number}/reviews`, `/pulls/{pull_number}/comments`) |
| 표시 내용 | PR 제목, 본문, 리뷰어(멘토) 코멘트, 인라인 코멘트, 승인/요청 상태 |
| 갱신 주기 | 최초 등록 시 수집 + 24시간마다 자동 갱신 (Spring Scheduler) |
| 권한 | Public 레포만 지원 (MVP), Private 레포는 Phase 2 |
| Rate Limit 대응 | GitHub Personal Access Token 또는 GitHub App 인증으로 5,000 req/hr 확보 |

#### F-05. 부트캠프 비교

| 항목 | 내용 |
|------|------|
| 비교 수 | 최대 3개 동시 비교 |
| 비교 항목 | 수강료, 기간, 트랙, 프로젝트 수, 평균 코드리뷰 수, 평균 별점, 기술 스택 분포 |
| UI | 테이블형 비교 + 레이더 차트 |

#### F-06. 평가 및 리뷰

| 항목 | 내용 |
|------|------|
| 평가 주체 | 로그인 사용자 (`STUDENT` 이상) |
| 평가 대상 | 부트캠프 |
| 항목 | 별점(1~5), 텍스트 리뷰(선택, 최대 500자) |
| 중복 방지 | 사용자당 부트캠프 1회 평가 (수정 가능) |
| 검증 | 해당 부트캠프 소속 수강생만 리뷰 가능 (소속 인증 후) |

---

### 5-2. Phase 2 — 확장 기능

#### F-07. Private 레포 코드리뷰 연동
- 수강생이 GitHub App을 통해 권한 위임
- Private 레포의 PR/리뷰 데이터 수집 가능

#### F-08. 부트캠프 공식 채널
- 운영사가 공지, 채용 연계, 수강생 모집 공고 등록
- 공식 배지(Verified) 부여

#### F-09. 프로젝트 좋아요 / 북마크
- 취준생이 관심 프로젝트 저장
- 마이페이지에서 북마크 목록 관리

#### F-10. 추천 알고리즘
- 사용자 열람 이력 기반 부트캠프/프로젝트 추천

#### F-11. 멘토 프로필
- 멘토의 코드리뷰 총 횟수, 평균 코멘트 수, 활동 부트캠프 노출

#### F-12. 통계 대시보드 (운영사용)
- 자사 프로젝트 누적 조회수, 좋아요 수, 리뷰 변화 추이

---

## 6. System Architecture Overview

### 6-1. 기술 스택

| 레이어 | 기술 | 버전 | 비고                       |
|--------|------|------|--------------------------|
| Frontend | Next.js | 15.x (App Router) | TypeScript, Tailwind CSS |
| Frontend 상태관리 | Zustand | 5.x | 전역 상태                    |
| Frontend 데이터 페칭 | TanStack Query | 5.x | 서버 상태, 캐싱                |
| Backend | Spring Boot | 4.0.x | Java 25, Gradle          |
| ORM | Spring Data JPA | - | QueryDSL 병행              |
| DB (개발) | H2 | - | In-memory, 콘솔 활성화        |
| DB (프로덕션) | PostgreSQL | 16.x | -                        |
| 인증 | Spring Security + JWT | - | OAuth2 Client (GitHub)   |
| 외부 API | GitHub REST API v3 | - | Octokit 또는 RestTemplate  |
| 스케줄러 | Spring Scheduler | - | @Scheduled, 코드리뷰 갱신      |
| 파일 스토리지 | AWS S3 / Cloudflare R2 | - | 이미지 업로드                  |
| 배포 (FE) | Vercel | - | -                        |
| 배포 (BE) | AWS EC2 / Railway | - | Docker                   |

### 6-2. 시스템 구성도 (텍스트)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client (Browser)                         │
│                    Next.js 15 (App Router)                      │
│         Pages: /, /bootcamps, /bootcamps/[id],                  │
│                /projects, /projects/[id], /compare              │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTPS / REST API
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Spring Boot 4.0.x (API Server)                │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐ │
│  │ Auth Module  │  │  Bootcamp    │  │   Project Module      │ │
│  │ (JWT/OAuth2) │  │  Module      │  │   + GitHub Sync       │ │
│  └──────────────┘  └──────────────┘  └───────────────────────┘ │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐ │
│  │  Review      │  │  Compare     │  │   Admin Module        │ │
│  │  Module      │  │  Module      │  │                       │ │
│  └──────────────┘  └──────────────┘  └───────────────────────┘ │
│                                                                 │
│           Spring Scheduler (GitHub 코드리뷰 갱신 Job)           │
└──────────┬───────────────────────┬──────────────────────────────┘
           │                       │
           ▼                       ▼
┌──────────────────┐    ┌─────────────────────────┐
│  H2 / PostgreSQL │    │   GitHub REST API v3     │
│  (JPA/Hibernate) │    │   (PR, Reviews, Comments)│
└──────────────────┘    └─────────────────────────┘
```

### 6-3. 디렉토리 구조 (제안)

```
openbootcamp/
├── frontend/                          # Next.js 15
│   ├── app/
│   │   ├── (public)/
│   │   │   ├── page.tsx               # 메인 홈
│   │   │   ├── bootcamps/
│   │   │   │   ├── page.tsx           # 부트캠프 탐색
│   │   │   │   └── [id]/page.tsx      # 부트캠프 상세
│   │   │   ├── projects/
│   │   │   │   ├── page.tsx           # 프로젝트 쇼케이스
│   │   │   │   └── [id]/page.tsx      # 프로젝트 상세
│   │   │   └── compare/page.tsx       # 부트캠프 비교
│   │   ├── (auth)/
│   │   │   └── login/page.tsx
│   │   └── (dashboard)/
│   │       ├── my/page.tsx            # 마이페이지
│   │       └── admin/page.tsx         # 운영사 대시보드
│   ├── components/
│   ├── lib/
│   └── types/
│
└── backend/                           # Spring Boot 4.0.x
    └── src/main/java/com/openbootcamp/
        ├── auth/
        ├── bootcamp/
        ├── project/
        ├── github/
        ├── review/
        └── common/
```

---

## 7. Data Model

### 7-1. ERD (핵심 엔티티)

```
┌──────────────┐     ┌──────────────────┐     ┌─────────────────────┐
│    User      │     │   BootcampMember │     │      Bootcamp        │
│──────────────│     │──────────────────│     │─────────────────────│
│ id (PK)      │1───*│ id (PK)          │*───1│ id (PK)             │
│ githubId     │     │ userId (FK)      │     │ name                │
│ username     │     │ bootcampId (FK)  │     │ slug                │
│ email        │     │ cohort           │     │ logoUrl             │
│ avatarUrl    │     │ trackId (FK)  ───┼──┐  │ description         │
│ role         │     │ verifiedAt       │  │  │ officialUrl         │
│ createdAt    │     └──────────────────┘  │  │ createdAt           │
│ updatedAt    │                           │  │ updatedAt           │
└──────────────┘                           │  └──────────┬──────────┘
       │                                   │             │1
       │                                   │             │
       │                                   │    ┌────────┴─────────────┐
       │                                   │    │    BootcampTrack      │
       │                                   └───*│──────────────────────│
       │                                        │ id (PK)              │
       │                                        │ bootcampId (FK)      │
       │                                        │ trackType            │  -- TrackType Enum
       │                                        │ operationType        │  -- OperationType Enum
       │                                        │ techStacks[]         │  -- TechStack Enum[]
       │                                        │ priceMin             │
       │                                        │ priceMax             │
       │                                        │ durationWeeks        │
       │                                        │ isRecruiting         │
       │                                        │ createdAt            │
       │                                        │ updatedAt            │
       │                                        └──────────────────────┘
       │
       │     ┌──────────────┐
       │     │   Project    │
       │1───*│──────────────│*─────────────────────────────1(Bootcamp)
       │     │ id (PK)      │
       │     │ userId (FK)  │
       │     │ bootcampId   │
       │     │ repoUrl      │
       │     │ repoOwner    │
       │     │ repoName     │
       │     │ title        │
       │     │ description  │
       │     │ techStacks[] │  -- TechStack Enum[]
       │     │ domainTag    │
       │     │ stars        │
       │     │ forks        │
       │     │ language     │
       │     │ thumbnailUrl │
       │     │ status       │
       │     │ cohort       │
       │     │ syncedAt     │
       │     │ createdAt    │
       │     │ updatedAt    │
       │     └──────┬───────┘
       │            │1
       │            │
       │     ┌──────┴───────────┐
       │     │   PullRequest    │
       │     │──────────────────│
       │     │ id (PK)          │
       │     │ projectId (FK)   │
       │     │ prNumber         │
       │     │ title            │
       │     │ body             │
       │     │ state            │
       │     │ authorGithubId   │
       │     │ createdAt        │
       │     └──────┬───────────┘
       │            │1
       │            │
       │     ┌──────┴───────────┐
       │     │   CodeReview     │
       │     │──────────────────│
       │     │ id (PK)          │
       │     │ pullRequestId    │
       │     │ reviewerGithub   │
       │     │ state            │  -- APPROVED / CHANGES_REQUESTED / COMMENTED
       │     │ body             │
       │     │ submittedAt      │
       │     └──────────────────┘
       │
       │     ┌──────────────────┐
       │1───*│  BootcampReview  │
             │──────────────────│
             │ id (PK)          │
             │ userId (FK)      │
             │ bootcampId (FK)  │
             │ rating           │  -- 1~5
             │ content          │
             │ createdAt        │
             │ updatedAt        │
             └──────────────────┘
```

### 7-2. 핵심 엔티티 상세

#### Enum 정의

```java
// TrackType.java — 부트캠프 트랙 종류
public enum TrackType {
    BACKEND, FRONTEND, FULLSTACK, MOBILE, DATA, DEVOPS, AI_ML, SECURITY, GAME
}

// OperationType.java — 수업 운영 형태
public enum OperationType {
    ONLINE, OFFLINE, HYBRID
}

// TechStack.java — 기술 스택 (트랙별 설정 + 프로젝트 태그 공용)
public enum TechStack {
    // Backend
    JAVA, KOTLIN, PYTHON, NODE_JS, GO, RUBY,
    SPRING_BOOT, DJANGO, FLASK, FASTAPI, EXPRESS, NEST_JS,

    // Frontend
    JAVASCRIPT, TYPESCRIPT,
    REACT, NEXT_JS, VUE, NUXT_JS, ANGULAR, SVELTE,

    // Mobile
    ANDROID, IOS, REACT_NATIVE, FLUTTER,

    // Database
    MYSQL, POSTGRESQL, MONGODB, REDIS, SQLITE, ORACLE,

    // DevOps / Infra
    DOCKER, KUBERNETES, AWS, GCP, AZURE, CI_CD, LINUX, NGINX,

    // AI / Data
    PYTORCH, TENSORFLOW, SCIKIT_LEARN, PANDAS, SPARK,

    // Etc
    GRAPHQL, REST_API, WEBSOCKET, KAFKA, ELASTICSEARCH
}

// ProjectStatus.java
public enum ProjectStatus {
    DRAFT, PUBLISHED, ARCHIVED
}

// Role.java
public enum Role {
    STUDENT, BOOTCAMP_ADMIN, ADMIN
}
```

---

#### User
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String githubId;        // GitHub OAuth sub

    @Column(unique = true, nullable = false)
    private String username;

    private String email;
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Role role;              // STUDENT, BOOTCAMP_ADMIN, ADMIN

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Bootcamp
```java
@Entity
@Table(name = "bootcamps")
public class Bootcamp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;            // URL-friendly name (e.g., "wecode")

    private String logoUrl;

    @Column(length = 500)
    private String description;

    private String officialUrl;

    @OneToMany(mappedBy = "bootcamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BootcampTrack> tracks = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### BootcampTrack
```java
@Entity
@Table(name = "bootcamp_tracks")
public class BootcampTrack {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bootcamp_id", nullable = false)
    private Bootcamp bootcamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackType trackType;    // BACKEND, FRONTEND, FULLSTACK, ...

    @Enumerated(EnumType.STRING)
    private OperationType operationType;  // ONLINE, OFFLINE, HYBRID

    @ElementCollection
    @CollectionTable(name = "bootcamp_track_tech_stacks",
                     joinColumns = @JoinColumn(name = "bootcamp_track_id"))
    @Column(name = "tech_stack")
    @Enumerated(EnumType.STRING)
    private List<TechStack> techStacks = new ArrayList<>();

    private Integer priceMin;       // 만원 단위
    private Integer priceMax;
    private Integer durationWeeks;
    private Boolean isRecruiting;   // 현재 모집 중 여부

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Project
```java
@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bootcamp bootcamp;

    @Column(nullable = false)
    private String repoUrl;

    private String repoOwner;
    private String repoName;
    private String title;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "project_tech_stacks",
                     joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech_stack")
    @Enumerated(EnumType.STRING)
    private List<TechStack> techStacks = new ArrayList<>();  // GitHub topics → TechStack 매핑

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

#### BootcampReview
```java
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

---

## 8. API Endpoints

> **Base URL**: `/api/v1`
>
> | 표시 | 의미 |
> |------|------|
> | `[PUBLIC]` | 비로그인(VISITOR) 포함 누구나 접근 가능 |
> | `[AUTH]` | GitHub OAuth 로그인 후 접근 가능 (JWT 필수) |
> | `[BOOTCAMP_ADMIN]` | BOOTCAMP_ADMIN 또는 ADMIN 역할 필요 |
> | `[ADMIN]` | ADMIN 역할만 접근 가능 |

### 8-1. 인증 (Auth)

```
POST   /auth/github/callback  [PUBLIC]   GitHub OAuth 콜백 → JWT 발급
POST   /auth/refresh          [PUBLIC]   Refresh Token → Access Token 재발급
POST   /auth/logout           [AUTH]     로그아웃 (RT 무효화)
GET    /auth/me               [AUTH]     현재 로그인 사용자 정보
```

### 8-2. 부트캠프 (Bootcamps)

```
GET    /bootcamps             [PUBLIC]   부트캠프 목록 조회 (필터/정렬/페이징)
  Query params:
    - trackType: string (BACKEND|FRONTEND|FULLSTACK|MOBILE|DATA|DEVOPS|AI_ML)
    - operationType: string (ONLINE|OFFLINE|HYBRID)
    - techStack: string (TechStack Enum 값)
    - keyword: string
    - sort: string (latest|projects|rating)
    - page: int (default: 0)
    - size: int (default: 20)

GET    /bootcamps/{id}                  [PUBLIC]   부트캠프 상세 조회 (트랙 목록 포함)
GET    /bootcamps/{id}/tracks           [PUBLIC]   부트캠프 트랙 목록 조회
GET    /bootcamps/{id}/projects         [PUBLIC]   해당 부트캠프 프로젝트 목록
GET    /bootcamps/{id}/reviews          [PUBLIC]   해당 부트캠프 리뷰 목록
GET    /bootcamps/{id}/stats            [PUBLIC]   해당 부트캠프 통계 (프로젝트 수, 평균 별점 등)
GET    /bootcamps/compare?ids=1,2,3     [PUBLIC]   최대 3개 부트캠프 비교 데이터

POST   /bootcamps                       [AUTH][BOOTCAMP_ADMIN]  부트캠프 등록
PUT    /bootcamps/{id}                  [AUTH][BOOTCAMP_ADMIN]  부트캠프 수정
DELETE /bootcamps/{id}                  [AUTH][ADMIN]           부트캠프 삭제

POST   /bootcamps/{id}/tracks           [AUTH][BOOTCAMP_ADMIN]  트랙 추가
PUT    /bootcamps/{id}/tracks/{trackId} [AUTH][BOOTCAMP_ADMIN]  트랙 수정
DELETE /bootcamps/{id}/tracks/{trackId} [AUTH][BOOTCAMP_ADMIN]  트랙 삭제
```

### 8-3. 프로젝트 (Projects)

```
GET    /projects              [PUBLIC]   프로젝트 목록 조회 (필터/정렬/페이징)
  Query params:
    - bootcampId: long
    - techStack: string (TechStack Enum 값)
    - domain: string
    - sort: string (latest|stars|reviews)
    - page: int (default: 0)
    - size: int (default: 20)

GET    /projects/{id}                             [PUBLIC]   프로젝트 상세 조회
GET    /projects/{id}/pull-requests               [PUBLIC]   프로젝트 PR 목록
GET    /projects/{id}/pull-requests/{prNumber}/reviews  [PUBLIC]  코드리뷰 목록

POST   /projects              [AUTH]    프로젝트 등록
  Body: { repoUrl, bootcampId, cohort, teamType, title, description, techStacks, domainTag }

PUT    /projects/{id}         [AUTH]    프로젝트 수정 (본인만)
DELETE /projects/{id}         [AUTH]    프로젝트 삭제 (본인 또는 ADMIN)
POST   /projects/{id}/sync    [AUTH]    GitHub 데이터 수동 재동기화
```

### 8-4. 코드리뷰 (GitHub Sync)

```
GET    /pull-requests/{id}/reviews              [PUBLIC]   코드리뷰 조회
POST   /github/sync/project/{projectId}         [AUTH]     특정 프로젝트 GitHub 데이터 즉시 동기화
GET    /github/rate-limit                       [ADMIN]    GitHub API Rate Limit 현황
```

### 8-5. 리뷰/평가 (Reviews)

```
GET    /reviews/bootcamp/{bootcampId}           [PUBLIC]   부트캠프 리뷰 목록

POST   /reviews/bootcamp/{bootcampId}           [AUTH]     부트캠프 리뷰 등록
  Body: { rating, content }

PUT    /reviews/{id}                            [AUTH]     리뷰 수정 (본인만)
DELETE /reviews/{id}                            [AUTH]     리뷰 삭제 (본인 또는 ADMIN)
```

### 8-6. 사용자 (Users)

```
GET    /users/me                        [AUTH]  내 프로필
PUT    /users/me                        [AUTH]  프로필 수정
GET    /users/me/projects               [AUTH]  내 프로젝트 목록
GET    /users/me/bookmarks              [AUTH]  북마크 목록 (Phase 2)
POST   /users/me/bootcamp-membership    [AUTH]  부트캠프 소속 인증 요청
```

### 8-7. 관리자 (Admin)

```
GET    /admin/users                     [ADMIN]  사용자 목록
PUT    /admin/users/{id}/role           [ADMIN]  역할 변경
GET    /admin/bootcamps/pending         [ADMIN]  승인 대기 부트캠프
PUT    /admin/bootcamps/{id}/approve    [ADMIN]  부트캠프 승인
GET    /admin/github/jobs               [ADMIN]  스케줄러 작업 현황
```

### 8-8. 응답 형식 (공통)

```json
// 성공 응답
{
  "success": true,
  "data": { ... },
  "message": null
}

// 페이징 응답
{
  "success": true,
  "data": {
    "content": [ ... ],
    "totalElements": 100,
    "totalPages": 5,
    "page": 0,
    "size": 20
  }
}

// 에러 응답
{
  "success": false,
  "data": null,
  "message": "에러 메시지",
  "errorCode": "RESOURCE_NOT_FOUND"
}
```

---

## 9. UI/UX Requirements

### 9-1. 핵심 화면 목록

| 화면 ID | 화면명 | Route | 설명 |
|---------|--------|-------|------|
| S-01 | 메인 홈 | `/` | 플랫폼 소개, 최신 프로젝트, 주목받는 부트캠프 |
| S-02 | 부트캠프 탐색 | `/bootcamps` | 필터+검색+정렬, 카드 그리드 |
| S-03 | 부트캠프 상세 | `/bootcamps/[id]` | 소개, 프로젝트 목록, 리뷰, 통계 |
| S-04 | 프로젝트 쇼케이스 | `/projects` | 전체 프로젝트 탐색 |
| S-05 | 프로젝트 상세 | `/projects/[id]` | 프로젝트 소개, PR/코드리뷰 목록 |
| S-06 | 코드리뷰 상세 | `/projects/[id]/pr/[prNumber]` | PR + 인라인 코멘트 뷰 |
| S-07 | 부트캠프 비교 | `/compare` | 3개 부트캠프 나란히 비교 |
| S-08 | 로그인 | `/login` | GitHub OAuth 버튼 |
| S-09 | 프로젝트 등록 | `/projects/new` | GitHub URL 입력 + 메타데이터 수정 |
| S-10 | 마이페이지 | `/my` | 내 프로젝트, 리뷰, 소속 인증 |
| S-11 | 운영사 대시보드 | `/admin/bootcamp` | 자사 통계, 프로젝트 목록 |

---

### 9-2. 화면별 와이어프레임 설명

#### S-01. 메인 홈 (`/`)

```
┌──────────────────────────────────────────────────────────┐
│  [Logo] OpenBootCamp          [부트캠프 탐색] [로그인]   │
├──────────────────────────────────────────────────────────┤
│                                                          │
│         Hero Section                                     │
│   "부트캠프 실력, 직접 확인하세요"                        │
│   [부트캠프 탐색하기]  [프로젝트 보기]                   │
│   통계: 부트캠프 N개 · 프로젝트 N개 · 코드리뷰 N건       │
│                                                          │
├──────────────────────────────────────────────────────────┤
│  📌 최신 프로젝트                           [더보기 →]   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐               │
│  │ 프로젝트 │ │ 프로젝트 │ │ 프로젝트 │               │
│  │ 카드     │ │ 카드     │ │ 카드     │               │
│  └──────────┘ └──────────┘ └──────────┘               │
│                                                          │
├──────────────────────────────────────────────────────────┤
│  🏫 주목받는 부트캠프                       [더보기 →]   │
│  ┌───────────────────────────────────────────────────┐  │
│  │ [로고] 부트캠프명  프로젝트 N개  ★ 4.5  [상세보기]│  │
│  │ [로고] 부트캠프명  프로젝트 N개  ★ 4.3  [상세보기]│  │
│  └───────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

---

#### S-02. 부트캠프 탐색 (`/bootcamps`)

```
┌──────────────────────────────────────────────────────────┐
│  [검색창: 부트캠프 이름 검색]                            │
│  필터: [트랙 ▼] [운영형태 ▼] [기술스택 ▼]  정렬: [▼]   │
├─────────────────┬────────────────────────────────────────┤
│                 │  ┌──────────┐ ┌──────────┐           │
│  필터 사이드바  │  │ 부트캠프 │ │ 부트캠프 │           │
│                 │  │ 카드     │ │ 카드     │           │
│  트랙           │  │ [로고]   │ │ [로고]   │           │
│  ☐ 백엔드       │  │ 이름     │ │ 이름     │           │
│  ☐ 프론트       │  │ 태그들   │ │ 태그들   │           │
│  ☐ 풀스택       │  │ ★4.5 N개│ │ ★4.3 N개│           │
│                 │  └──────────┘ └──────────┘           │
│  운영형태       │                                        │
│  ☐ 온라인       │  ┌──────────┐ ┌──────────┐           │
│  ☐ 오프라인     │  │ ...      │ │ ...      │           │
│  ☐ 혼합         │  └──────────┘ └──────────┘           │
│                 │                                        │
│  기술스택       │           [페이지네이션]               │
│  [자동완성입력] │                                        │
└─────────────────┴────────────────────────────────────────┘
```

---

#### S-03. 부트캠프 상세 (`/bootcamps/[id]`)

```
┌──────────────────────────────────────────────────────────┐
│  [로고] 부트캠프명                                        │
│  태그: 백엔드 | 온라인 | 12주 | 200만원~              │
│  [공식 사이트 →]  [비교하기 추가]                        │
├──────────────────────────────────────────────────────────┤
│  탭: [프로젝트(N)] [코드리뷰(N)] [수강 리뷰(N)] [통계]  │
├──────────────────────────────────────────────────────────┤
│  // 프로젝트 탭 활성화 시                                │
│  기수 필터: [전체] [1기] [2기] ...                       │
│                                                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐               │
│  │프로젝트  │ │프로젝트  │ │프로젝트  │               │
│  │썸네일    │ │썸네일    │ │썸네일    │               │
│  │제목      │ │제목      │ │제목      │               │
│  │기술태그  │ │기술태그  │ │기술태그  │               │
│  │★ PR 5건 │ │★ PR 3건 │ │★ PR 7건 │               │
│  └──────────┘ └──────────┘ └──────────┘               │
└──────────────────────────────────────────────────────────┘
```

---

#### S-05. 프로젝트 상세 (`/projects/[id]`)

```
┌──────────────────────────────────────────────────────────┐
│  [← 목록으로]                                            │
│  [썸네일 이미지]                                          │
│  프로젝트 제목                                            │
│  [GitHub ↗] | 소속: OO부트캠프 3기 | 기술: Java Spring  │
│                                                          │
│  프로젝트 소개 (README 렌더링)                            │
│  ...                                                     │
│                                                          │
├──────────────────────────────────────────────────────────┤
│  📋 코드리뷰 / Pull Requests (N건)                       │
│  ┌────────────────────────────────────────────────────┐  │
│  │ PR #12 "사용자 인증 기능 구현"                      │  │
│  │ 리뷰어: @mentor_kim  상태: APPROVED                │  │
│  │ 코멘트 3건 | 2024-11-15                           │  │
│  │ [코드리뷰 상세 보기 →]                             │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ PR #11 "상품 목록 API 개발"                        │  │
│  │ 리뷰어: @mentor_kim  상태: CHANGES_REQUESTED       │  │
│  │ 코멘트 8건 | 2024-11-10                           │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

---

#### S-07. 부트캠프 비교 (`/compare`)

```
┌──────────────────────────────────────────────────────────┐
│  부트캠프 비교  (최대 3개)                               │
│  [부트캠프 추가 +]                                        │
├───────────────────┬──────────────┬──────────────┬────────┤
│  항목             │  A 부트캠프  │  B 부트캠프  │ C 부트 │
├───────────────────┼──────────────┼──────────────┼────────┤
│  수강료           │  200만~250만  │  180만~220만  │  ...   │
│  기간             │  12주         │  16주         │  ...   │
│  트랙             │  백엔드       │  풀스택       │  ...   │
│  프로젝트 수      │  47개         │  32개         │  ...   │
│  코드리뷰 수      │  213건        │  98건         │  ...   │
│  평균 별점        │  ★★★★☆ 4.3  │  ★★★★★ 4.7  │  ...   │
│  기술 스택 분포   │  [레이더차트] │  [레이더차트] │  ...   │
├───────────────────┴──────────────┴──────────────┴────────┤
│  [A 부트캠프 상세 보기]  [B 부트캠프 상세 보기]  [...]  │
└──────────────────────────────────────────────────────────┘
```

---

#### S-09. 프로젝트 등록 (`/projects/new`)

```
┌──────────────────────────────────────────────────────────┐
│  프로젝트 등록                                            │
│                                                          │
│  GitHub 레포지토리 URL                                    │
│  [https://github.com/username/repo      ] [불러오기]    │
│                                                          │
│  // 불러오기 성공 시 자동 입력:                          │
│  프로젝트 제목: [repo 이름 자동입력 (수정 가능)]         │
│  설명: [repo description 자동입력 (수정 가능)]           │
│  주요 언어: Java (자동 감지)                              │
│                                                          │
│  소속 부트캠프 [선택 ▼]                                  │
│  수강 기수 [1기 ▼]                                       │
│  팀/개인 [개인 ▼]                                        │
│                                                          │
│  기술 스택 태그 [Spring Boot × ] [React × ] [추가 +]    │
│  도메인 [커머스 ▼]                                       │
│                                                          │
│  대표 이미지 [파일 선택 또는 URL 입력]                   │
│                                                          │
│  [임시저장]  [공개 등록]                                 │
└──────────────────────────────────────────────────────────┘
```

---

## 10. 개발 우선순위 및 마일스톤

### Milestone 1 — 백엔드 기반
- [ ] 프로젝트 초기 설정 (Spring Boot 4.0.x, H2, JPA)
- [ ] User, Bootcamp, Project 엔티티 및 Repository 구현
- [ ] GitHub OAuth2 로그인 + JWT 발급
- [ ] 부트캠프 CRUD API
- [ ] 프로젝트 등록 API + GitHub REST API 연동 (메타데이터 수집)

### Milestone 2 — 코드리뷰 연동
- [ ] PullRequest, CodeReview 엔티티 및 Repository
- [ ] GitHub PR/Review 수집 서비스
- [ ] Spring Scheduler: 24시간 자동 갱신 Job
- [ ] 코드리뷰 조회 API

### Milestone 3 — 프론트엔드 기반
- [ ] Next.js 15 프로젝트 초기 설정
- [ ] 공통 레이아웃, 헤더/푸터 컴포넌트
- [ ] S-01 메인 홈
- [ ] S-02 부트캠프 탐색
- [ ] S-03 부트캠프 상세
- [ ] S-08 로그인 (GitHub OAuth)

### Milestone 4 — 핵심 기능 완성
- [ ] S-04/05/06 프로젝트 쇼케이스 + 상세 + 코드리뷰
- [ ] S-07 부트캠프 비교
- [ ] S-09 프로젝트 등록
- [ ] S-10 마이페이지
- [ ] 평가/리뷰 기능

### Milestone 5 — 운영 및 Phase 2
- [ ] S-11 운영사 대시보드
- [ ] 관리자 기능 (승인, 역할 관리)
- [ ] PostgreSQL 마이그레이션
- [ ] 배포 (Vercel + Railway/EC2)
- [ ] Phase 2 기능 순차 적용

---

> **참고**: 이 PRD는 Claude Code CDD(Context-Driven Development) 방식으로 작성되었습니다.
> 코드 생성 시 각 섹션을 컨텍스트로 제공하면 일관된 구현이 가능합니다.
> 예시: "PRD의 F-03 프로젝트 등록 스펙과 7-2 Project 엔티티를 참고하여 Spring Boot 서비스 클래스를 작성해주세요."
