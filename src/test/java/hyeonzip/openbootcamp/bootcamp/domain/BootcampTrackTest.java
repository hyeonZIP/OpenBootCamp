package hyeonzip.openbootcamp.bootcamp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hyeonzip.openbootcamp.bootcamp.fixture.BootcampFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture;
import hyeonzip.openbootcamp.common.enums.TechStack;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BootcampTrackTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 BootcampTrack을 생성하면 필드가 올바르게 설정된다")
    void builder_setsFieldsCorrectly() {
        List<TechStack> techStacks = List.of(TechStack.JAVA, TechStack.SPRING_BOOT);

        var request = BootcampTrackRequestFixture.createRequest();

        BootcampTrack track = BootcampTrack.create(request.trackType(), request.operationType(),
            techStacks, request.priceMin(), request.priceMax(), request.durationWeeks(),
            request.isRecruiting());

        assertThat(track.getTrackType()).isEqualTo(request.trackType());
        assertThat(track.getOperationType()).isEqualTo(request.operationType());
        assertThat(track.getTechStacks()).isEqualTo(techStacks);
        assertThat(track.getPriceMin()).isEqualTo(request.priceMin());
        assertThat(track.getPriceMax()).isEqualTo(request.priceMax());
        assertThat(track.getDurationWeeks()).isEqualTo(request.durationWeeks());
        assertThat(track.getIsRecruiting()).isEqualTo(request.isRecruiting());
    }

    @Test
    @DisplayName("techStacks를 null로 전달하면 빈 리스트로 초기화된다")
    void builder_withNullTechStacks_initializesEmptyList() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        assertThat(track.getTechStacks()).isNotNull();
        assertThat(track.getTechStacks()).isEmpty();
    }

    @Test
    @DisplayName("update() 호출 시 모든 필드가 갱신된다")
    void update_updatesAllFields() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        var request = BootcampTrackRequestFixture.updateRequestWithTechStacks();

        track.update(request.trackType(), request.operationType(), request.techStacks(),
            request.priceMin(), request.priceMax(), request.durationWeeks(),
            request.isRecruiting());

        assertThat(track.getTrackType()).isEqualTo(request.trackType());
        assertThat(track.getOperationType()).isEqualTo(request.operationType());
        assertThat(track.getTechStacks()).isEqualTo(request.techStacks());
        assertThat(track.getPriceMin()).isEqualTo(request.priceMin());
        assertThat(track.getPriceMax()).isEqualTo(request.priceMax());
        assertThat(track.getDurationWeeks()).isEqualTo(request.durationWeeks());
        assertThat(track.getIsRecruiting()).isEqualTo(request.isRecruiting());
    }

    @Test
    @DisplayName("update() 호출 시 techStacks에 null을 전달하면 빈 리스트로 설정된다")
    void update_withNullTechStacks_setsEmptyList() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        var request = BootcampTrackRequestFixture.updateRequest();

        track.update(request.trackType(), request.operationType(), null, null, null, null, null);

        assertThat(track.getTechStacks()).isNotNull();
        assertThat(track.getTechStacks()).isEmpty();
    }

    @Test
    @DisplayName("update() 호출 시 null 값으로도 갱신할 수 있다")
    void update_withNullOptionalFields() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        var request = BootcampTrackRequestFixture.updateRequest();

        track.update(request.trackType(), null, List.of(), null, null, null, null);

        assertThat(track.getTrackType()).isEqualTo(request.trackType());
        assertThat(track.getOperationType()).isNull();
        assertThat(track.getPriceMin()).isNull();
        assertThat(track.getPriceMax()).isNull();
        assertThat(track.getDurationWeeks()).isNull();
        assertThat(track.getIsRecruiting()).isNull();
    }

    @Test
    @DisplayName("초기 상태에서 bootcamp 참조는 null이다")
    void newTrack_bootcampReferenceIsNull() {
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        assertThat(track.getBootcamp()).isNull();
    }

    @Test
    @DisplayName("assignBootcamp()으로 부트캠프 참조가 설정된다")
    void assignBootcamp_setsBootcampReference() {
        Bootcamp bootcamp = BootcampFixture.bootcamp();
        BootcampTrack track = BootcampTrackFixture.bootcampTrack();

        bootcamp.addTrack(track);

        assertThat(track.getBootcamp()).isSameAs(bootcamp);
    }
}
