package hyeonzip.openbootcamp.bootcamp.domain;

import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BootcampTrackTest {

    @Test
    @DisplayName("빌더로 BootcampTrack을 생성하면 필드가 올바르게 설정된다")
    void builder_setsFieldsCorrectly() {
        List<TechStack> techStacks = List.of(TechStack.JAVA, TechStack.SPRING_BOOT);

        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .techStacks(techStacks)
                .priceMin(100)
                .priceMax(200)
                .durationWeeks(12)
                .isRecruiting(true)
                .build();

        assertThat(track.getTrackType()).isEqualTo(TrackType.BACKEND);
        assertThat(track.getOperationType()).isEqualTo(OperationType.ONLINE);
        assertThat(track.getTechStacks()).containsExactly(TechStack.JAVA, TechStack.SPRING_BOOT);
        assertThat(track.getPriceMin()).isEqualTo(100);
        assertThat(track.getPriceMax()).isEqualTo(200);
        assertThat(track.getDurationWeeks()).isEqualTo(12);
        assertThat(track.getIsRecruiting()).isTrue();
    }

    @Test
    @DisplayName("techStacks를 null로 전달하면 빈 리스트로 초기화된다")
    void builder_withNullTechStacks_initializesEmptyList() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .techStacks(null)
                .build();

        assertThat(track.getTechStacks()).isNotNull();
        assertThat(track.getTechStacks()).isEmpty();
    }

    @Test
    @DisplayName("techStacks 없이 생성하면 빈 리스트로 초기화된다")
    void builder_withoutTechStacks_initializesEmptyList() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.FRONTEND)
                .build();

        assertThat(track.getTechStacks()).isNotNull();
        assertThat(track.getTechStacks()).isEmpty();
    }

    @Test
    @DisplayName("update() 호출 시 모든 필드가 갱신된다")
    void update_updatesAllFields() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .techStacks(List.of(TechStack.JAVA))
                .priceMin(100)
                .priceMax(200)
                .durationWeeks(12)
                .isRecruiting(true)
                .build();

        track.update(
                TrackType.FULLSTACK,
                OperationType.HYBRID,
                List.of(TechStack.JAVASCRIPT, TechStack.REACT),
                150,
                300,
                24,
                false
        );

        assertThat(track.getTrackType()).isEqualTo(TrackType.FULLSTACK);
        assertThat(track.getOperationType()).isEqualTo(OperationType.HYBRID);
        assertThat(track.getTechStacks()).containsExactly(TechStack.JAVASCRIPT, TechStack.REACT);
        assertThat(track.getPriceMin()).isEqualTo(150);
        assertThat(track.getPriceMax()).isEqualTo(300);
        assertThat(track.getDurationWeeks()).isEqualTo(24);
        assertThat(track.getIsRecruiting()).isFalse();
    }

    @Test
    @DisplayName("update() 호출 시 techStacks에 null을 전달하면 빈 리스트로 설정된다")
    void update_withNullTechStacks_setsEmptyList() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .techStacks(List.of(TechStack.JAVA))
                .build();

        track.update(TrackType.BACKEND, OperationType.ONLINE, null, null, null, null, null);

        assertThat(track.getTechStacks()).isNotNull();
        assertThat(track.getTechStacks()).isEmpty();
    }

    @Test
    @DisplayName("update() 호출 시 null 값으로도 갱신할 수 있다")
    void update_withNullOptionalFields() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .operationType(OperationType.ONLINE)
                .priceMin(100)
                .priceMax(200)
                .durationWeeks(12)
                .isRecruiting(true)
                .build();

        track.update(TrackType.BACKEND, null, List.of(), null, null, null, null);

        assertThat(track.getOperationType()).isNull();
        assertThat(track.getPriceMin()).isNull();
        assertThat(track.getPriceMax()).isNull();
        assertThat(track.getDurationWeeks()).isNull();
        assertThat(track.getIsRecruiting()).isNull();
    }

    @Test
    @DisplayName("초기 상태에서 bootcamp 참조는 null이다")
    void newTrack_bootcampReferenceIsNull() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .build();

        assertThat(track.getBootcamp()).isNull();
    }

    @Test
    @DisplayName("assignBootcamp()으로 부트캠프 참조가 설정된다")
    void assignBootcamp_setsBootcampReference() {
        Bootcamp bootcamp = Bootcamp.builder()
                .name("위코드")
                .slug("wecode")
                .build();
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.BACKEND)
                .build();

        bootcamp.addTrack(track);

        assertThat(track.getBootcamp()).isSameAs(bootcamp);
    }

    @Test
    @DisplayName("trackType만으로도 빌더 생성이 가능하다")
    void builder_withOnlyTrackType() {
        BootcampTrack track = BootcampTrack.builder()
                .trackType(TrackType.DATA)
                .build();

        assertThat(track.getTrackType()).isEqualTo(TrackType.DATA);
        assertThat(track.getOperationType()).isNull();
        assertThat(track.getPriceMin()).isNull();
        assertThat(track.getPriceMax()).isNull();
        assertThat(track.getDurationWeeks()).isNull();
        assertThat(track.getIsRecruiting()).isNull();
    }
}
