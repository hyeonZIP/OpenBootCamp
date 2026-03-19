package hyeonzip.openbootcamp.bootcamp.fixture;

import hyeonzip.openbootcamp.bootcamp.dto.BootcampRequest;
import java.util.List;

public final class BootcampRequestFixture {

    public static final String NAME = "코드스테이츠";
    public static final String ENGLISH_NAME = "-Codestates-";
    public static final String LOGO_URL = "http://logo.png";
    public static final String DESCRIPTION = "풀스택 부트캠프";
    public static final String OFFICIAL_URL = "https://codestates.com";
    public static final String OTHER_NAME = "위코드 Pro";
    public static final String OTHER_ENGLISH_NAME = "Wecode Pro";
    public static final String OTHER_LOGO_URL = "http://other-logo.jpg";
    public static final String OTHER_DESCRIPTION = "다른 설명";
    public static final String OTHER_OFFICIAL_URL = "https://wecode.co.kr";

    private BootcampRequestFixture() {
    }

    public static BootcampRequest createRequestWithTracks() {
        var tracks = List.of(BootcampTrackRequestFixture.createRequest());

        return new BootcampRequest(NAME, ENGLISH_NAME, LOGO_URL, DESCRIPTION, OFFICIAL_URL, tracks);
    }

    public static BootcampRequest updateRequest() {

        return new BootcampRequest(OTHER_NAME, OTHER_ENGLISH_NAME, OTHER_LOGO_URL,
            OTHER_DESCRIPTION, OTHER_OFFICIAL_URL, null);
    }

    public static BootcampRequest createOtherRequestWithTracks() {
        var tracks = List.of(BootcampTrackRequestFixture.createRequest());

        return new BootcampRequest(OTHER_NAME, OTHER_ENGLISH_NAME, OTHER_LOGO_URL,
            OTHER_DESCRIPTION, OTHER_OFFICIAL_URL, tracks);
    }

    public static BootcampRequest createOtherRequestWithTracksNull() {

        return new BootcampRequest(OTHER_NAME, OTHER_ENGLISH_NAME, OTHER_LOGO_URL,
            OTHER_DESCRIPTION, OTHER_OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidNameRequest(String name) {

        return new BootcampRequest(name, OTHER_ENGLISH_NAME, OTHER_LOGO_URL, OTHER_DESCRIPTION,
            OTHER_OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidSlugRequest(String englishName) {

        return new BootcampRequest(OTHER_NAME, englishName, OTHER_LOGO_URL, OTHER_DESCRIPTION,
            OTHER_OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidEnglishNameRequest(String englishName) {

        return new BootcampRequest(NAME, englishName, LOGO_URL, DESCRIPTION, OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidLogoUrlRequest(String logoUrl) {

        return new BootcampRequest(NAME, ENGLISH_NAME, logoUrl, DESCRIPTION, OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidOfficialUrlRequest(String officialUrl) {

        return new BootcampRequest(NAME, ENGLISH_NAME, LOGO_URL, DESCRIPTION, officialUrl, null);
    }
}
