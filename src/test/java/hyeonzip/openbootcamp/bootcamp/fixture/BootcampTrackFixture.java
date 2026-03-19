package hyeonzip.openbootcamp.bootcamp.fixture;

import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture.DURATION_WEEKS;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture.IS_RECRUITING;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture.OPERATION_TYPE;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture.PRICE_MAX;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture.PRICE_MIN;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture.TRACK_TYPE;

import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;

public final class BootcampTrackFixture {

    private BootcampTrackFixture() {
    }

    public static BootcampTrack bootcampTrack() {
        return BootcampTrack.create(
            TRACK_TYPE,
            OPERATION_TYPE,
            null,
            PRICE_MIN,
            PRICE_MAX,
            DURATION_WEEKS,
            IS_RECRUITING
        );
    }
}
