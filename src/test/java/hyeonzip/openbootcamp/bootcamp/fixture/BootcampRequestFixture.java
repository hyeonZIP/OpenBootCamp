package hyeonzip.openbootcamp.bootcamp.fixture;

import hyeonzip.openbootcamp.bootcamp.dto.BootcampRequest;
import java.util.List;

/**
 * BootcampRequest DTO 테스트용 픽스처.
 * <p>여러 테스트 파일에서 동일하게 반복되는 요청 객체를 하나의 출처로 관리한다.</p>
 */
public final class BootcampRequestFixture {

    public static final String NAME = "코드스테이츠";
    public static final String ENGLISH_NAME = "-Codestates-";
    public static final String DESCRIPTION = "풀스택 부트캠프";
    public static final String OFFICIAL_URL = "https://codestates.com";
    public static final String UPDATE_NAME = "위코드 Pro";
    public static final String UPDATE_ENGLISH_NAME = "Wecode Pro";
    public static final String UPDATE_DESCRIPTION = "업데이트된 설명";
    public static final String UPDATE_OFFICIAL_URL = "https://wecode.co.kr";

    private BootcampRequestFixture() {
    }

    public static BootcampRequest createRequest() {
        var tracks = List.of(BootcampTrackRequestFixture.createRequest());

        return new BootcampRequest(NAME, ENGLISH_NAME, null, DESCRIPTION, OFFICIAL_URL, tracks);
    }

    public static BootcampRequest updateRequest(){

        return new BootcampRequest(UPDATE_NAME, UPDATE_ENGLISH_NAME, null, UPDATE_DESCRIPTION, UPDATE_OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidNameRequest(String name){

        return new BootcampRequest(name, ENGLISH_NAME, null, DESCRIPTION, OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidEnglishNameRequest(String englishName){

        return new BootcampRequest(NAME, englishName, null, DESCRIPTION, OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidLogoUrlRequest(String logoUrl){

        return new BootcampRequest(NAME, ENGLISH_NAME, logoUrl, DESCRIPTION, OFFICIAL_URL, null);
    }

    public static BootcampRequest invalidOfficialUrlRequest(String officialUrl){

        return new BootcampRequest(NAME, ENGLISH_NAME, null, DESCRIPTION, officialUrl, null);
    }
}
