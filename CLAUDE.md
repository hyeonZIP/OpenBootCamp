# OpenBootCamp — 코딩 관례 (CLAUDE.md)

## 1. 개발 순서 원칙

기능 구현 시 레이어를 **낮은 단계부터 하나씩** 만들 것. 절대 한 번에 전부 만들지 말 것.

```
순서: Domain → Repository → Service → Controller
```

- 하나의 레이어 구현 완료 후 해당 레이어 테스트 작성
- 테스트까지 완료된 후에 다음 레이어로 진행
- 완료 시 사용자에게 알리고 다음 단계 진행 여부 확인

---

## 2. 패키지 구조

```
{domain}/
├── controller/
├── domain/
├── dto/
├── repository/
└── service/
    ├── ports/
    │   └── inp/
    │       └── {Domain}Service.java       ← 인터페이스
    └── Default{Domain}Service.java        ← 구현체
```

`common/`
```
common/
├── config/         (JpaAuditingConfig 등)
├── enums/
├── exception/      (ErrorCode, OpenBootCampException, GlobalExceptionHandler)
├── response/       (ApiResponse)
└── util/           (SlugUtils 등)
```

---

## 3. Service 계층 인터페이스 분리

- **인터페이스**: `{domain}/service/ports/inp/{Domain}Service.java`
  - `@Service` 없음, 메서드 시그니처만 선언
- **구현체**: `{domain}/service/Default{Domain}Service.java`
  - `implements {Domain}Service`
  - `@Service`, `@RequiredArgsConstructor`, `@Transactional(readOnly = true)` 부착
  - 쓰기 메서드에는 개별 `@Transactional` 추가
  - 모든 메서드에 `@Override` 명시
- Controller, Test 등에서는 **인터페이스 타입**으로 주입

---

## 4. 코드 스타일

### 삼항연산자 금지
```java
// ❌
this.value = condition ? a : b;

// ✅
if (condition) {
    this.value = a;
} else {
    this.value = b;
}
```

### JPA Criteria API 금지
`Specification`, `JpaSpecificationExecutor`, `CriteriaBuilder` 사용 금지.

동적 쿼리는 JPQL `@Query` + `:param IS NULL OR` 패턴으로 대체:
```java
@Query("""
    SELECT b FROM Bootcamp b
    WHERE (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:trackType IS NULL OR EXISTS (
        SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND bt.trackType = :trackType))
    """)
Page<Bootcamp> findByFilters(@Param("keyword") String keyword,
                              @Param("trackType") TrackType trackType,
                              Pageable pageable);
```

복잡한 동적 쿼리가 불가피한 경우 **구현 전에 사용자에게 먼저 알릴 것** (향후 QueryDSL/jooq 도입 예정).

---

## 5. 테스트 관례

### Domain 테스트 — 단위 테스트
- 순수 Java 단위 테스트, Spring 컨텍스트 로드 없음
- 엔티티 생성, 상태 변경 메서드 동작 검증

### Repository 테스트
- `@DataJpaTest` 사용
- `TestEntityManager`로 데이터 세팅

### Service 테스트 — 통합 테스트
- **`@SpringBootTest` + `@Transactional`** 사용
- `@Mock`, `@InjectMocks`, `MockitoExtension` 사용 금지
- `@Autowired`로 실제 Bean 주입, H2 DB에 직접 데이터 저장
- 영속성 컨텍스트 충돌이 예상되는 테스트에서는 `@PersistenceContext EntityManager` + `entityManager.clear()` 활용
