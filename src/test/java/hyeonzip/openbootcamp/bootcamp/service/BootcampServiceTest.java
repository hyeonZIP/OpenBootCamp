package hyeonzip.openbootcamp.bootcamp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampResponse;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackResponse;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampTrackRepository;
import hyeonzip.openbootcamp.bootcamp.service.ports.inp.BootcampService;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TrackType;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BootcampServiceTest {

    @Autowired
    private BootcampService bootcampService;

    @Autowired
    private BootcampRepository bootcampRepository;

    @Autowired
    private BootcampTrackRepository bootcampTrackRepository;

    @Autowired
    private EntityManager em;

    private Bootcamp savedBootcamp;

    @BeforeEach
    void setUp() {
        savedBootcamp = bootcampRepository.save(
            Bootcamp.create("Wecode",
                Slug.from("wecode"),
                null,
                "백엔드 부트캠프",
                "https://wecode.co.kr")
        );
        savedBootcamp.addTrack(
            BootcampTrack.create(
                TrackType.BACKEND,
                OperationType.ONLINE,
                null,
                150,
                200,
                12,
                true)
        );
        em.flush();
        em.clear();
    }

    // ── 등록 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 등록 성공")
    void createBootcamp_success() {
        BootcampTrackRequest trackRequest = new BootcampTrackRequest(
            TrackType.FRONTEND, OperationType.HYBRID, null, 100, 150, 8, true);
        BootcampRequest request = new BootcampRequest(
            "코드스테이츠", "Codestates", "https://logo.png", "풀스택 부트캠프", "https://codestates.com",
            List.of(trackRequest));

        BootcampResponse result = bootcampService.createBootcamp(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("코드스테이츠");
        assertThat(result.slug()).isEqualTo("codestates");
        assertThat(result.tracks()).hasSize(1);
        assertThat(result.tracks().get(0).trackType()).isEqualTo(TrackType.FRONTEND);
    }

    @Test
    @DisplayName("트랙 없이 부트캠프 등록 성공")
    void createBootcamp_withoutTracks_success() {
        BootcampRequest request = new BootcampRequest(
            "엘리스", "elice", null, null, null, null);

        BootcampResponse result = bootcampService.createBootcamp(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("엘리스");
        assertThat(result.tracks()).isEmpty();
    }

    @Test
    @DisplayName("중복 영문명(slug)으로 등록 시 예외 발생")
    void createBootcamp_duplicateSlug_throwsException() {
        bootcampRepository.save(
            Bootcamp.builder().name("코드스테이츠").slug(Slug.from("codestates")).build());

        BootcampRequest request = new BootcampRequest("코드스테이츠2", "Codestates", null, null, null,
            null);

        assertThatThrownBy(() -> bootcampService.createBootcamp(request))
            .isInstanceOf(OpenBootCampException.class);
    }

    @Test
    @DisplayName("중복 이름으로 등록 시 예외 발생")
    void createBootcamp_duplicateName_throwsException() {
        BootcampRequest request = new BootcampRequest("Wecode", "wecode-unique", null, null, null,
            null);

        assertThatThrownBy(() -> bootcampService.createBootcamp(request))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── 단건 조회 ──────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 단건 조회 시 트랙 포함 반환")
    void getBootcamp_returnsWithTracks() {
        BootcampResponse result = bootcampService.getBootcamp(savedBootcamp.getId());

        assertThat(result.id()).isEqualTo(savedBootcamp.getId());
        assertThat(result.name()).isEqualTo("Wecode");
        assertThat(result.tracks()).hasSize(1);
        assertThat(result.tracks().get(0).trackType()).isEqualTo(TrackType.BACKEND);
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프 조회 시 예외 발생")
    void getBootcamp_notFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.getBootcamp(999L))
            .isInstanceOf(OpenBootCampException.class);
    }

    @Test
    @DisplayName("slug로 부트캠프 조회 시 트랙 포함 반환")
    void getBootcampBySlug_returnsWithTracks() {
        BootcampResponse result = bootcampService.getBootcampBySlug(
            savedBootcamp.getSlug().getValue());

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo(savedBootcamp.getName());
        assertThat(result.slug()).isEqualTo(savedBootcamp.getSlug().getValue());
        assertThat(result.tracks()).hasSize(savedBootcamp.getTracks().size());
        assertThat(result.tracks().getFirst().trackType()).isEqualTo(TrackType.BACKEND);
    }

    @Test
    @DisplayName("존재하지 않는 slug로 조회 시 예외 발생")
    void getBootcampBySlug_notFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.getBootcampBySlug("nonexistent"))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── 목록 조회 ──────────────────────────────────────────────────

    @Test
    @DisplayName("전체 목록 조회 성공")
    void getBootcamps_returnsAllBootcamps() {
        bootcampRepository.save(
            Bootcamp.builder().name("Codestates").slug(Slug.from("codestates")).build());

        Page<BootcampResponse> result = bootcampService.getBootcamps(
            null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("트랙 타입 필터로 조회 시 일치하는 부트캠프만 반환")
    void getBootcamps_filteredByTrackType() {
        Bootcamp other = bootcampRepository.save(
            Bootcamp.builder().name("Codestates").slug(Slug.from("codestates")).build()
        );
        other.addTrack(BootcampTrack.builder()
            .trackType(TrackType.FRONTEND)
            .operationType(OperationType.ONLINE)
            .build());
        bootcampRepository.flush();

        Page<BootcampResponse> result = bootcampService.getBootcamps(
            TrackType.BACKEND, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Wecode");
    }

    // ── 수정 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 수정 성공")
    void updateBootcamp_success() {
        BootcampRequest request = new BootcampRequest(
            "위코드 Pro", "Wecode Pro", null, "업데이트된 설명", null, null);

        BootcampResponse result = bootcampService.updateBootcamp(savedBootcamp.getId(), request);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("위코드 Pro");
        assertThat(result.slug()).isEqualTo("wecode-pro");
        assertThat(result.description()).isEqualTo("업데이트된 설명");
    }

    @Test
    @DisplayName("중복 영문명(slug)으로 수정 시 예외 발생")
    void updateBootcamp_duplicateSlug_throwsException() {
        bootcampRepository.save(
            Bootcamp.builder().name("코드스테이츠").slug(Slug.from("codestates")).build());

        BootcampRequest request = new BootcampRequest("Wecode", "Codestates", null, null, null,
            null);

        assertThatThrownBy(() -> bootcampService.updateBootcamp(savedBootcamp.getId(), request))
            .isInstanceOf(OpenBootCampException.class);
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프 수정 시 예외 발생")
    void updateBootcamp_notFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.updateBootcamp(999L,
            new BootcampRequest("New Name", "new-name", null, null, null, null)))
            .isInstanceOf(OpenBootCampException.class);
    }

    @Test
    @DisplayName("다른 부트캠프와 중복된 이름으로 수정 시 예외 발생")
    void updateBootcamp_duplicateName_throwsException() {
        bootcampRepository.save(
            Bootcamp.builder().name("Codestates").slug(Slug.from("codestates")).build());

        assertThatThrownBy(() -> bootcampService.updateBootcamp(savedBootcamp.getId(),
            new BootcampRequest("Codestates", "codestates-other", null, null, null, null)))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── 삭제 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 삭제 성공")
    void deleteBootcamp_success() {
        bootcampService.deleteBootcamp(savedBootcamp.getId());

        assertThat(bootcampRepository.existsById(savedBootcamp.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프 삭제 시 예외 발생")
    void deleteBootcamp_notFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.deleteBootcamp(999L))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── 트랙 추가 ─────────────────────────────────────────────────

    @Test
    @DisplayName("트랙 추가 성공")
    void addTrack_success() {
        BootcampTrackRequest request = new BootcampTrackRequest(
            TrackType.FULLSTACK, OperationType.OFFLINE, null, 200, 300, 16, false);

        BootcampTrackResponse result = bootcampService.addTrack(savedBootcamp.getId(), request);

        assertThat(result.id()).isNotNull();
        assertThat(result.trackType()).isEqualTo(TrackType.FULLSTACK);
        assertThat(bootcampRepository.findWithTracksById(savedBootcamp.getId())
            .get().getTracks()).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프에 트랙 추가 시 예외 발생")
    void addTrack_bootcampNotFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.addTrack(999L,
            new BootcampTrackRequest(TrackType.BACKEND, OperationType.ONLINE, null, null, null,
                null, null)))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────

    @Test
    @DisplayName("트랙 수정 성공")
    void updateTrack_success() {
        BootcampTrack track = savedBootcamp.getTracks().get(0);
        BootcampTrackRequest request = new BootcampTrackRequest(
            TrackType.FRONTEND, OperationType.OFFLINE, null, 100, 150, 8, false);

        BootcampTrackResponse result = bootcampService.updateTrack(
            savedBootcamp.getId(), track.getId(), request);

        assertThat(result.id()).isNotNull();
        assertThat(result.trackType()).isEqualTo(TrackType.FRONTEND);
        assertThat(result.operationType()).isEqualTo(OperationType.OFFLINE);
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프의 트랙 수정 시 예외 발생")
    void updateTrack_bootcampNotFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.updateTrack(999L, 1L,
            new BootcampTrackRequest(TrackType.BACKEND, OperationType.ONLINE, null, null, null,
                null, null)))
            .isInstanceOf(OpenBootCampException.class);
    }

    @Test
    @DisplayName("다른 부트캠프의 트랙 수정 시 예외 발생")
    void updateTrack_trackNotFound_throwsException() {
        BootcampTrack track = savedBootcamp.getTracks().get(0);

        Bootcamp other = bootcampRepository.save(
            Bootcamp.builder().name("Codestates").slug(Slug.from("codestates")).build());

        assertThatThrownBy(() -> bootcampService.updateTrack(other.getId(), track.getId(),
            new BootcampTrackRequest(TrackType.BACKEND, OperationType.ONLINE, null, null, null,
                null, null)))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── 트랙 삭제 ─────────────────────────────────────────────────

    @Test
    @DisplayName("트랙 삭제 성공")
    void deleteTrack_success() {
        Long bootcampId = savedBootcamp.getId();
        Long trackId = savedBootcamp.getTracks().get(0).getId();

        bootcampService.deleteTrack(bootcampId, trackId);

        assertThat(bootcampTrackRepository.existsById(trackId)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프의 트랙 삭제 시 예외 발생")
    void deleteTrack_bootcampNotFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.deleteTrack(999L, 1L))
            .isInstanceOf(OpenBootCampException.class);
    }
}
