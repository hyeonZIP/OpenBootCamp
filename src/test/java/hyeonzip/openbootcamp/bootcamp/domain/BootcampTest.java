package hyeonzip.openbootcamp.bootcamp.domain;

import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TrackType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BootcampTest {

    @Test
    @DisplayName("빌더로 Bootcamp를 생성하면 필드가 올바르게 설정된다")
    void builder_setsFieldsCorrectly() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .logoUrl("https://logo.png")
                .description("백엔드 부트캠프")
                .officialUrl("https://wecode.co.kr")
                .build();

        assertThat(bootcamp.getName()).isEqualTo("위코드");
        assertThat(bootcamp.getSlug()).isEqualTo("wecode");
        assertThat(bootcamp.getLogoUrl()).isEqualTo("https://logo.png");
        assertThat(bootcamp.getDescription()).isEqualTo("백엔드 부트캠프");
        assertThat(bootcamp.getOfficialUrl()).isEqualTo("https://wecode.co.kr");
    }

    @Test
    @DisplayName("Bootcamp 생성 시 트랙 목록은 빈 리스트로 초기화된다")
    void builder_initializes_tracksAsEmptyList() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();

        assertThat(bootcamp.getTracks()).isNotNull();
        assertThat(bootcamp.getTracks()).isEmpty();
    }

    @Test
    @DisplayName("update() 호출 시 모든 필드가 갱신된다")
    void update_updatesAllFields() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("원래이름")
                .slug("original-slug")
                .logoUrl("https://old-logo.png")
                .description("이전 설명")
                .officialUrl("https://old.co.kr")
                .build();

        bootcamp.update("새이름", "new-slug", "https://new-logo.png", "새 설명", "https://new.co.kr");

        assertThat(bootcamp.getName()).isEqualTo("새이름");
        assertThat(bootcamp.getSlug()).isEqualTo("new-slug");
        assertThat(bootcamp.getLogoUrl()).isEqualTo("https://new-logo.png");
        assertThat(bootcamp.getDescription()).isEqualTo("새 설명");
        assertThat(bootcamp.getOfficialUrl()).isEqualTo("https://new.co.kr");
    }

    @Test
    @DisplayName("addTrack() 호출 시 트랙이 목록에 추가된다")
    void addTrack_addsTrackToList() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .build();

        bootcamp.addTrack(track);

        assertThat(bootcamp.getTracks()).hasSize(1);
        assertThat(bootcamp.getTracks()).contains(track);
    }

    @Test
    @DisplayName("addTrack() 호출 시 트랙에 부트캠프 참조가 설정된다")
    void addTrack_assignsBootcampReference() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.FRONTEND)
                .build();

        bootcamp.addTrack(track);

        assertThat(track.getBootcamp()).isSameAs(bootcamp);
    }

    @Test
    @DisplayName("addTrack()을 여러 번 호출하면 모든 트랙이 추가된다")
    void addTrack_multipleTracksAdded() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();

        bootcamp.addTrack(BootcampTrack.builder().trackType(TrackType.BACKEND).build());
        bootcamp.addTrack(BootcampTrack.builder().trackType(TrackType.FRONTEND).build());
        bootcamp.addTrack(BootcampTrack.builder().trackType(TrackType.FULLSTACK).build());

        assertThat(bootcamp.getTracks()).hasSize(3);
    }

    @Test
    @DisplayName("update() 호출 시 null 값으로도 갱신할 수 있다")
    void update_withNullValues() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .logoUrl("https://logo.png")
                .description("설명")
                .officialUrl("https://wecode.co.kr")
                .build();

        bootcamp.update("위코드", "wecode", null, null, null);

        assertThat(bootcamp.getLogoUrl()).isNull();
        assertThat(bootcamp.getDescription()).isNull();
        assertThat(bootcamp.getOfficialUrl()).isNull();
    }

    @Test
    @DisplayName("빌더에서 name, slug만 설정해도 객체가 생성된다")
    void builder_withMinimalFields() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();

        assertThat(bootcamp.getName()).isEqualTo("위코드");
        assertThat(bootcamp.getSlug()).isEqualTo("wecode");
        assertThat(bootcamp.getLogoUrl()).isNull();
        assertThat(bootcamp.getDescription()).isNull();
        assertThat(bootcamp.getOfficialUrl()).isNull();
    }

    @Test
    @DisplayName("tracks 목록은 외부에서 직접 추가해도 반영된다 (컬렉션 참조 동일성)")
    void tracks_listReferenceIsSameAfterAddTrack() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();
        List<BootcampTrack> tracksRef = bootcamp.getTracks();
        BootcampTrack track = BootcampTrack.builder().trackType(TrackType.BACKEND).build();

        bootcamp.addTrack(track);

        assertThat(tracksRef).isSameAs(bootcamp.getTracks());
        assertThat(tracksRef).hasSize(1);
    }
}
