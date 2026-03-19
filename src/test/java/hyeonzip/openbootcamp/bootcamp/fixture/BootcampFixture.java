package hyeonzip.openbootcamp.bootcamp.fixture;

import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.DESCRIPTION;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.ENGLISH_NAME;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.LOGO_URL;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.NAME;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.OFFICIAL_URL;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.OTHER_DESCRIPTION;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.OTHER_ENGLISH_NAME;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.OTHER_NAME;
import static hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture.OTHER_OFFICIAL_URL;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;

public final class BootcampFixture {

    private BootcampFixture() {
    }

    public static Bootcamp bootcamp() {

        return Bootcamp.create(NAME, Slug.from(ENGLISH_NAME), ENGLISH_NAME, LOGO_URL, DESCRIPTION,
            OFFICIAL_URL);
    }

    public static Bootcamp otherBootcamp() {

        return Bootcamp.create(OTHER_NAME, Slug.from(OTHER_ENGLISH_NAME), OTHER_ENGLISH_NAME, null,
            OTHER_DESCRIPTION, OTHER_OFFICIAL_URL);
    }

    public static Bootcamp onlyNullableFieldBootcamp() {

        return Bootcamp.create(NAME, Slug.from(ENGLISH_NAME), ENGLISH_NAME, null, null, null);
    }
}
