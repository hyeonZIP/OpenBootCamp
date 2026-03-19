package hyeonzip.openbootcamp.bootcamp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampResponse;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampTrackRepository;
import hyeonzip.openbootcamp.bootcamp.service.ports.inp.BootcampService;
import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BootcampServiceTest {

    private static final Long NON_EXISTING_BOOTCAMP_ID = 999L;

    @Autowired
    private BootcampService bootcampService;

    @Autowired
    private BootcampRepository bootcampRepository;

    @Autowired
    private BootcampTrackRepository bootcampTrackRepository;

    @Autowired
    private EntityManager entityManager;

    private Bootcamp savedBootcamp;

    @BeforeEach
    void setUp() {
        savedBootcamp = bootcampRepository.save(BootcampFixture.bootcamp());
        savedBootcamp.addTrack(BootcampTrackFixture.bootcampTrack());
        entityManager.flush();
        entityManager.clear();
    }

    // ── 등록 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 등록 성공")
    void createBootcamp_success() {
        var request = BootcampRequestFixture.createOtherRequestWithTracks();

        BootcampResponse result = bootcampService.createBootcamp(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.slug()).isEqualTo(Slug.from(request.englishName()).getValue());
        assertThat(result.englishName()).isEqualTo(request.englishName());
        assertThat(result.tracks()).hasSize(request.tracks().size());
        assertThat(result.tracks().getFirst().trackType()).isEqualTo(
            request.tracks().getFirst().trackType());
    }

    @Test
    @DisplayName("트랙 없이 부트캠프 등록 성공")
    void createBootcamp_withoutTracks_success() {
        var request = BootcampRequestFixture.createOtherRequestWithTracksNull();

        BootcampResponse result = bootcampService.createBootcamp(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tracks()).isEmpty();
    }

    @Test
    @DisplayName("등록 시 englishName은 응답에 원본 그대로 포함된다")
    void createBootcamp_englishNamePreservedInResponse() {
        var request = BootcampRequestFixture.createOtherRequestWithTracks();

        BootcampResponse result = bootcampService.createBootcamp(request);

        assertThat(result.englishName()).isEqualTo(request.englishName());
        assertThat(result.slug()).isNotEqualTo(request.englishName());
    }

    @Test
    @DisplayName("중복 영문명(slug)으로 등록 시 예외 발생")
    void createBootcamp_duplicateSlug_throwsException() {
        var request = BootcampRequestFixture.invalidSlugRequest(savedBootcamp.getSlug().getValue());

        assertThatThrownBy(() -> bootcampService.createBootcamp(request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_SLUG_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("중복 이름으로 등록 시 예외 발생")
    void createBootcamp_duplicateName_throwsException() {
        var request = BootcampRequestFixture.invalidNameRequest(savedBootcamp.getName());

        assertThatThrownBy(() -> bootcampService.createBootcamp(request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NAME_DUPLICATE.getMessage());
    }

    // ── 단건 조회 ──────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 단건 조회 시 트랙 포함 반환")
    void getBootcamp_returnsWithTracks() {
        var result = bootcampService.getBootcamp(savedBootcamp.getId());

        assertThat(result.id()).isEqualTo(savedBootcamp.getId());
        assertThat(result.name()).isEqualTo(savedBootcamp.getName());
        assertThat(result.englishName()).isEqualTo(savedBootcamp.getEnglishName());
        assertThat(result.tracks()).hasSize(savedBootcamp.getTracks().size());
        assertThat(result.tracks().getFirst().trackType()).isEqualTo(
            savedBootcamp.getTracks().getFirst().getTrackType());
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프 조회 시 예외 발생")
    void getBootcamp_notFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.getBootcamp(NON_EXISTING_BOOTCAMP_ID))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("slug로 부트캠프 조회 시 트랙 포함 반환")
    void getBootcampBySlug_returnsWithTracks() {
        var result = bootcampService.getBootcampBySlug(savedBootcamp.getSlug().getValue());

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo(savedBootcamp.getName());
        assertThat(result.slug()).isEqualTo(savedBootcamp.getSlug().getValue());
        assertThat(result.englishName()).isEqualTo(savedBootcamp.getEnglishName());
        assertThat(result.tracks()).hasSize(savedBootcamp.getTracks().size());
        assertThat(result.tracks().getFirst().trackType()).isEqualTo(
            savedBootcamp.getTracks().getFirst().getTrackType());
    }

    @Test
    @DisplayName("존재하지 않는 slug로 조회 시 예외 발생")
    void getBootcampBySlug_notFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.getBootcampBySlug("nonexistent"))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }

    // ── 목록 조회 ──────────────────────────────────────────────────

    @Test
    @DisplayName("전체 목록 조회 성공")
    void getBootcamps_returnsAllBootcamps() {
        bootcampRepository.save(BootcampFixture.otherBootcamp());

        var result = bootcampService.getBootcamps(
            null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("트랙 타입 필터로 조회 시 일치하는 부트캠프만 반환")
    void getBootcamps_filteredByTrackType() {
        Bootcamp other = bootcampRepository.save(BootcampFixture.otherBootcamp());
        other.addTrack(BootcampTrackFixture.bootcampTrack());

        var result = bootcampService.getBootcamps(
            null, null, null, other.getName(), PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo(other.getName());
    }

    // ── 수정 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("부트캠프 수정 성공")
    void updateBootcamp_success() {
        var request = BootcampRequestFixture.updateRequest();
        var result = bootcampService.updateBootcamp(savedBootcamp.getId(), request);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.slug()).isEqualTo(Slug.from(request.englishName()).getValue());
        assertThat(result.englishName()).isEqualTo(request.englishName());
        assertThat(result.description()).isEqualTo(request.description());
    }

    @Test
    @DisplayName("수정 시 englishName이 새 값으로 갱신되어 응답에 포함된다")
    void updateBootcamp_englishNameUpdatedInResponse() {
        var request = BootcampRequestFixture.updateRequest();

        BootcampResponse result = bootcampService.updateBootcamp(savedBootcamp.getId(), request);

        assertThat(result.englishName()).isEqualTo(request.englishName());
        assertThat(result.englishName()).isNotEqualTo(savedBootcamp.getEnglishName());
    }

    @Test
    @DisplayName("중복 영문명(slug)으로 수정 시 예외 발생")
    void updateBootcamp_duplicateSlug_throwsException() {
        var other = bootcampRepository.save(BootcampFixture.otherBootcamp());
        var request = BootcampRequestFixture.invalidSlugRequest(savedBootcamp.getSlug().getValue());

        assertThatThrownBy(() -> bootcampService.updateBootcamp(other.getId(), request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_SLUG_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프 수정 시 예외 발생")
    void updateBootcamp_notFound_throwsException() {
        var request = BootcampRequestFixture.updateRequest();

        assertThatThrownBy(() -> bootcampService.updateBootcamp(NON_EXISTING_BOOTCAMP_ID, request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("다른 부트캠프와 중복된 이름으로 수정 시 예외 발생")
    void updateBootcamp_duplicateName_throwsException() {
        var other = bootcampRepository.save(BootcampFixture.otherBootcamp());
        var request = BootcampRequestFixture.invalidNameRequest(other.getName());

        assertThatThrownBy(() -> bootcampService.updateBootcamp(savedBootcamp.getId(), request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NAME_DUPLICATE.getMessage());
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
        assertThatThrownBy(() -> bootcampService.deleteBootcamp(NON_EXISTING_BOOTCAMP_ID))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }

    // ── 트랙 추가 ─────────────────────────────────────────────────

    @Test
    @DisplayName("트랙 추가 성공")
    void addTrack_success() {
        var request = BootcampTrackRequestFixture.createRequest();
        var result = bootcampService.addTrack(savedBootcamp.getId(), request);
        var updatedBootcamp = bootcampRepository.findWithTracksById(savedBootcamp.getId());

        assertThat(result.id()).isNotNull();
        assertThat(result.trackType()).isEqualTo(request.trackType());
        assertThat(updatedBootcamp.get().getTracks()).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프에 트랙 추가 시 예외 발생")
    void addTrack_bootcampNotFound_throwsException() {
        var request = BootcampTrackRequestFixture.createRequest();

        assertThatThrownBy(() -> bootcampService.addTrack(NON_EXISTING_BOOTCAMP_ID, request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────

    @Test
    @DisplayName("트랙 수정 성공")
    void updateTrack_success() {
        BootcampTrack track = savedBootcamp.getTracks().getFirst();
        var request = BootcampTrackRequestFixture.updateRequest();
        var result = bootcampService.updateTrack(savedBootcamp.getId(), track.getId(), request);

        assertThat(result.id()).isNotNull();
        assertThat(result.trackType()).isEqualTo(request.trackType());
        assertThat(result.operationType()).isEqualTo(request.operationType());
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프의 트랙 수정 시 예외 발생")
    void updateTrack_bootcampNotFound_throwsException() {
        var request = BootcampTrackRequestFixture.updateRequest();

        assertThatThrownBy(() -> bootcampService.updateTrack(NON_EXISTING_BOOTCAMP_ID,
            savedBootcamp.getTracks().getFirst().getId(), request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("다른 부트캠프의 트랙 수정 시 예외 발생")
    void updateTrack_trackNotFound_throwsException() {
        BootcampTrack track = savedBootcamp.getTracks().getFirst();
        Bootcamp other = bootcampRepository.save(BootcampFixture.otherBootcamp());
        var request = BootcampTrackRequestFixture.updateRequest();

        assertThatThrownBy(() -> bootcampService.updateTrack(other.getId(), track.getId(), request))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    // ── 트랙 삭제 ─────────────────────────────────────────────────

    @Test
    @DisplayName("트랙 삭제 성공")
    void deleteTrack_success() {
        Long bootcampId = savedBootcamp.getId();
        Long trackId = savedBootcamp.getTracks().getFirst().getId();

        bootcampService.deleteTrack(bootcampId, trackId);

        assertThat(bootcampTrackRepository.existsById(trackId)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 부트캠프의 트랙 삭제 시 예외 발생")
    void deleteTrack_bootcampNotFound_throwsException() {
        assertThatThrownBy(() -> bootcampService.deleteTrack(NON_EXISTING_BOOTCAMP_ID,
            savedBootcamp.getTracks().getFirst().getId()))
            .isInstanceOf(OpenBootCampException.class)
            .hasMessage(ErrorCode.BOOTCAMP_NOT_FOUND.getMessage());
    }
}
