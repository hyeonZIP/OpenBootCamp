package hyeonzip.openbootcamp.bootcamp.fixture;

import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TrackType;

public final class BootcampTrackFixture {

    public static final TrackType TRACK_TYPE = TrackType.BACKEND;
    public static final OperationType OPERATION_TYPE = OperationType.ONLINE;
    public static final int PRICE_MIN = 150;
    public static final int PRICE_MAX = 200;
    public static final int DURATION_WEEKS = 12;

    private BootcampTrackFixture() {}

    public static BootcampTrack bootcampTrack() {
        return BootcampTrack.create(TRACK_TYPE, OPERATION_TYPE, null, PRICE_MIN, PRICE_MAX, DURATION_WEEKS, true);
    }
}
