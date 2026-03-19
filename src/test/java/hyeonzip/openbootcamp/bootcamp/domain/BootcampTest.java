package hyeonzip.openbootcamp.bootcamp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.bootcamp.fixture.BootcampFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BootcampTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 Bootcamp를 생성하면 필드가 올바르게 설정된다")
    void builder_setsFieldsCorrectly() {
        var request = BootcampRequestFixture.createRequestWithTracks();
        Bootcamp bootcamp = Bootcamp.create(request.name(), Slug.from(request.englishName()),
            request.englishName(), request.logoUrl(), request.description(), request.officialUrl());

        assertThat(bootcamp.getName()).isEqualTo(request.name());
        assertThat(bootcamp.getSlug().getValue()).isEqualTo(
            Slug.from(request.englishName()).getValue());
        assertThat(bootcamp.getEnglishName()).isEqualTo(request.englishName());
        assertThat(bootcamp.getLogoUrl()).isEqualTo(request.logoUrl());
        assertThat(bootcamp.getDescription()).isEqualTo(request.description());
        assertThat(bootcamp.getOfficialUrl()).isEqualTo(request.officialUrl());
    }

    @Test
    @DisplayName("Bootcamp 생성 시 트랙 목록은 빈 리스트로 초기화된다")
    void builder_initializes_tracksAsEmptyList() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();

        assertThat(bootcamp.getTracks()).isNotNull();
        assertThat(bootcamp.getTracks()).isEmpty();
    }

    @Test
    @DisplayName("update() 호출 시 모든 필드가 갱신된다")
    void update_updatesAllFields() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();

        var request = BootcampRequestFixture.updateRequest();

        bootcamp.update(request.name(), Slug.from(request.englishName()), request.englishName(),
            request.logoUrl(), request.description(), request.officialUrl());

        assertThat(bootcamp.getName()).isEqualTo(request.name());
        assertThat(bootcamp.getSlug().getValue()).isEqualTo(
            Slug.from(request.englishName()).getValue());
        assertThat(bootcamp.getLogoUrl()).isEqualTo(request.logoUrl());
        assertThat(bootcamp.getDescription()).isEqualTo(request.description());
        assertThat(bootcamp.getOfficialUrl()).isEqualTo(request.officialUrl());
    }

    @Test
    @DisplayName("addTrack() 호출 시 트랙이 목록에 추가된다")
    void addTrack_addsTrackToList() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        bootcamp.addTrack(track);

        assertThat(bootcamp.getTracks()).hasSize(1);
        assertThat(bootcamp.getTracks()).contains(track);
    }

    @Test
    @DisplayName("addTrack() 호출 시 트랙에 부트캠프 참조가 설정된다")
    void addTrack_assignsBootcampReference() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        bootcamp.addTrack(track);

        assertThat(track.getBootcamp()).isSameAs(bootcamp);
    }

    @Test
    @DisplayName("addTrack()을 여러 번 호출하면 모든 트랙이 추가된다")
    void addTrack_multipleTracksAdded() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        int loopCount = 3;
        for (int i = 0; i < loopCount; i++) {
            bootcamp.addTrack(track);
        }

        assertThat(bootcamp.getTracks()).hasSize(loopCount);
    }

    @Test
    @DisplayName("update() 호출 시 null 값으로도 갱신할 수 있다")
    void update_withNullValues() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        var request = BootcampRequestFixture.updateRequest();

        bootcamp.update(request.name(), Slug.from(request.englishName()), null, null, null, null);

        assertThat(bootcamp.getLogoUrl()).isNull();
        assertThat(bootcamp.getDescription()).isNull();
        assertThat(bootcamp.getOfficialUrl()).isNull();
    }

    @Test
    @DisplayName("빌더에서 name, slug, englishName만 설정해도 객체가 생성된다")
    void builder_withMinimalFields() {
        Bootcamp bootcamp = BootcampFixture.onlyNullableFieldBootcamp();

        assertThat(bootcamp.getName()).isNotNull();
        assertThat(bootcamp.getSlug().getValue()).isNotNull();
        assertThat(bootcamp.getEnglishName()).isNotNull();
        assertThat(bootcamp.getLogoUrl()).isNull();
        assertThat(bootcamp.getDescription()).isNull();
        assertThat(bootcamp.getOfficialUrl()).isNull();
    }

    @Test
    @DisplayName("englishName은 slug와 달리 원본 문자열 그대로 저장된다")
    void englishName_isPreservedAsIs_unlikeSlug() {
        String rawEnglishName = "Code States Pro";
        Bootcamp bootcamp = Bootcamp.create("코드스테이츠", Slug.from(rawEnglishName), rawEnglishName,
            null, null, null);

        assertThat(bootcamp.getEnglishName()).isEqualTo(rawEnglishName);
        assertThat(bootcamp.getSlug().getValue()).isNotEqualTo(rawEnglishName);
        assertThat(bootcamp.getSlug().getValue()).isEqualTo("code-states-pro");
    }

    @Test
    @DisplayName("update() 호출 시 englishName도 갱신된다")
    void update_updatesEnglishName() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        var request = BootcampRequestFixture.updateRequest();

        bootcamp.update(request.name(), Slug.from(request.englishName()), request.englishName(),
            request.logoUrl(), request.description(), request.officialUrl());

        assertThat(bootcamp.getEnglishName()).isEqualTo(request.englishName());
    }

    @Test
    @DisplayName("tracks 목록은 외부에서 직접 추가해도 반영된다 (컬렉션 참조 동일성)")
    void tracks_listReferenceIsSameAfterAddTrack() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        List<BootcampTrack> tracksRef = bootcamp.getTracks();
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();
        bootcamp.addTrack(track);

        assertThat(tracksRef).isSameAs(bootcamp.getTracks());
        assertThat(tracksRef).hasSize(1);
    }
}
