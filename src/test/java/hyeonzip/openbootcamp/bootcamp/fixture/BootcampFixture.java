package hyeonzip.openbootcamp.bootcamp.fixture;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;

public final class BootcampFixture {

    public static final String NAME = "Wecode";
    public static final String ENGLISH_NAME = "wecode";
    public static final String DESCRIPTION = "백엔드 부트캠프";
    public static final String OFFICIAL_URL = "https://wecode.co.kr";
    public static final String OTHER_NAME = "OTHER_Wecode";
    public static final String OTHER_ENGLISH_NAME = "OTHER_wecode";
    public static final String OTHER_DESCRIPTION = "OTHER_백엔드 부트캠프";
    public static final String OTHER_OFFICIAL_URL = "https://wecode.co123.kr";

    private BootcampFixture() {
    }

    public static Bootcamp bootcamp() {
        return Bootcamp.create(NAME, Slug.from(ENGLISH_NAME), null, DESCRIPTION, OFFICIAL_URL);
    }

    public static Bootcamp otherBootcamp() {
        return Bootcamp.create(OTHER_NAME, Slug.from(OTHER_ENGLISH_NAME), null, OTHER_DESCRIPTION,
            OTHER_OFFICIAL_URL);
    }
}
