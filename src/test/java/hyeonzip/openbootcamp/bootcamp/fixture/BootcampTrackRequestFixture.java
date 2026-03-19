package hyeonzip.openbootcamp.bootcamp.fixture;

import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackRequest;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import java.util.List;

public final class BootcampTrackRequestFixture {

    public static final TrackType TRACK_TYPE = TrackType.FRONTEND;
    public static final OperationType OPERATION_TYPE = OperationType.HYBRID;
    public static final List<TechStack> TECH_STACKS = List.of(TechStack.JAVASCRIPT,
        TechStack.REACT);
    public static final int PRICE_MIN = 100;
    public static final int PRICE_MAX = 150;
    public static final int DURATION_WEEKS = 8;
    public static final boolean IS_RECRUITING = true;
    public static final TrackType OTHER_TRACK_TYPE = TrackType.SECURITY;
    public static final OperationType OTHER_OPERATION_TYPE = OperationType.ONLINE;
    public static final int OTHER_PRICE_MIN = 200;
    public static final int OTHER_PRICE_MAX = 350;
    public static final int OTHER_DURATION_WEEKS = 10;
    public static final boolean OTHER_IS_RECRUITING = false;

    private BootcampTrackRequestFixture() {
    }

    public static BootcampTrackRequest createRequest() {
        return new BootcampTrackRequest(TRACK_TYPE, OPERATION_TYPE, null, PRICE_MIN, PRICE_MAX,
            DURATION_WEEKS, IS_RECRUITING);
    }

    public static BootcampTrackRequest updateRequest() {
        return new BootcampTrackRequest(OTHER_TRACK_TYPE, OTHER_OPERATION_TYPE, null,
            OTHER_PRICE_MIN, OTHER_PRICE_MAX, OTHER_DURATION_WEEKS, OTHER_IS_RECRUITING);
    }

    public static BootcampTrackRequest updateRequestWithTechStacks() {
        return new BootcampTrackRequest(OTHER_TRACK_TYPE, OTHER_OPERATION_TYPE, TECH_STACKS,
            OTHER_PRICE_MIN, OTHER_PRICE_MAX, OTHER_DURATION_WEEKS, OTHER_IS_RECRUITING);
    }

    public static BootcampTrackRequest invalidRequest() {
        return new BootcampTrackRequest(
            null, OperationType.ONLINE, null, null, null, null, null);
    }

    public static BootcampTrackRequest invalidPriceMinRequest() {
        return new BootcampTrackRequest(TRACK_TYPE, OPERATION_TYPE, null, -1, PRICE_MAX,
            DURATION_WEEKS, IS_RECRUITING);
    }

    public static BootcampTrackRequest invalidPriceMaxRequest() {
        return new BootcampTrackRequest(TRACK_TYPE, OPERATION_TYPE, null, PRICE_MIN, -1,
            DURATION_WEEKS, IS_RECRUITING);
    }

    public static BootcampTrackRequest invalidPriceRequest() {
        return new BootcampTrackRequest(TRACK_TYPE, OPERATION_TYPE, null, PRICE_MAX, PRICE_MIN,
            DURATION_WEEKS, IS_RECRUITING);
    }

    public static BootcampTrackRequest invalidDurationWeeksRequest(int durationWeeks) {
        return new BootcampTrackRequest(TRACK_TYPE, OPERATION_TYPE, null, PRICE_MIN, PRICE_MAX,
            durationWeeks, IS_RECRUITING);
    }

    public static BootcampTrackRequest validDurationWeeksRequest(Integer durationWeeks) {
        return new BootcampTrackRequest(TRACK_TYPE, OPERATION_TYPE, null, PRICE_MIN, PRICE_MAX,
            durationWeeks, IS_RECRUITING);
    }
}
