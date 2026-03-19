package hyeonzip.openbootcamp.bootcamp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampRequestFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackFixture;
import hyeonzip.openbootcamp.bootcamp.fixture.BootcampTrackRequestFixture;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BootcampControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BootcampRepository bootcampRepository;

    @Autowired
    private EntityManager entityManager;

    private Bootcamp savedBootcamp;

    @BeforeEach
    void setUp() {
        savedBootcamp = bootcampRepository.save(BootcampFixture.bootcamp());
        savedBootcamp.addTrack(BootcampTrackFixture.bootcampTrack());
        entityManager.flush();
        entityManager.clear();
    }

    // ── 목록 조회 ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /bootcamps - 200 목록 반환")
    void getBootcamps_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("GET /bootcamps?keyword=Wecode - 키워드 필터 적용")
    void getBootcamps_withKeyword_returnsFiltered() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps").param("keyword", savedBootcamp.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content[0].name").value(savedBootcamp.getName()));
    }

    @Test
    @DisplayName("GET /bootcamps?keyword=없는부트캠프 - 빈 목록 반환")
    void getBootcamps_withNoMatchKeyword_returnsEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps").param("keyword", "없는부트캠프"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").isEmpty());
    }

    // ── 단건 조회 ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /bootcamps/{id} - 200 트랙 포함 반환")
    void getBootcamp_returns200WithTracks() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps/{id}", savedBootcamp.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value(savedBootcamp.getName()))
            .andExpect(jsonPath("$.data.tracks").isArray())
            .andExpect(jsonPath("$.data.tracks[0].trackType").value(
                savedBootcamp.getTracks().getFirst().getTrackType().name()));
    }

    @Test
    @DisplayName("GET /bootcamps/{id} 존재하지 않는 ID - 404 반환")
    void getBootcamp_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps/{id}", 999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /bootcamps/slug/{slug} - 200 트랙 포함 반환")
    void getBootcampBySlug_returns200WithTracks() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps/slug/{slug}", savedBootcamp.getSlug().getValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value(savedBootcamp.getName()))
            .andExpect(jsonPath("$.data.slug").value(savedBootcamp.getSlug().getValue()))
            .andExpect(jsonPath("$.data.tracks").isArray())
            .andExpect(jsonPath("$.data.tracks[0].trackType").value(
                savedBootcamp.getTracks().getFirst().getTrackType().name()));
    }

    @Test
    @DisplayName("GET /bootcamps/slug/{slug} 존재하지 않는 slug - 404 반환")
    void getBootcampBySlug_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/bootcamps/slug/{slug}", "nonexistent-slug"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ── 등록 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /bootcamps - 201 등록 성공")
    void createBootcamp_returns201() throws Exception {
        var request = BootcampRequestFixture.createOtherRequestWithTracks();

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value(request.name()))
            .andExpect(jsonPath("$.data.slug").value(Slug.from(request.englishName()).getValue()))
            .andExpect(jsonPath("$.data.tracks[0].trackType").value(
                request.tracks().getFirst().trackType().name()));
    }

    @Test
    @DisplayName("POST /bootcamps 이름 누락 - 400 반환")
    void createBootcamp_blankName_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidNameRequest("");

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps name 30자 초과 - 400 반환")
    void createBootcamp_nameTooLong_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidNameRequest("A".repeat(31));

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps englishName 누락 - 400 반환")
    void createBootcamp_blankEnglishName_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidEnglishNameRequest("");

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps englishName 50자 초과 - 400 반환")
    void createBootcamp_englishNameTooLong_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidEnglishNameRequest("a".repeat(51));

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps englishName 한글 포함 - 400 반환")
    void createBootcamp_englishNameWithKorean_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidEnglishNameRequest("테스트camp");

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps logoUrl 유효하지 않은 URL - 400 반환")
    void createBootcamp_invalidLogoUrl_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidLogoUrlRequest("not-a-url");

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps officialUrl 누락 - 400 반환")
    void createBootcamp_blankOfficialUrl_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidOfficialUrlRequest("");

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps officialUrl 유효하지 않은 URL - 400 반환")
    void createBootcamp_invalidOfficialUrl_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidOfficialUrlRequest("not-a-url");

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps description 500자 초과 - 400 반환")
    void createBootcamp_descriptionTooLong_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidOfficialUrlRequest("A".repeat(501));

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps 중복 이름 - 409 반환")
    void createBootcamp_duplicateName_returns409() throws Exception {
        var request = BootcampRequestFixture.invalidNameRequest(savedBootcamp.getName());

        mockMvc.perform(post("/api/v1/bootcamps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ── 수정 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /bootcamps/{id} - 200 수정 성공")
    void updateBootcamp_returns200() throws Exception {
        var request = BootcampRequestFixture.updateRequest();

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value(request.name()))
            .andExpect(jsonPath("$.data.slug").value(Slug.from(request.englishName()).getValue()));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} name 30자 초과 - 400 반환")
    void updateBootcamp_nameTooLong_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidNameRequest("A".repeat(31));

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} englishName 누락 - 400 반환")
    void updateBootcamp_blankEnglishName_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidEnglishNameRequest("");

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} englishName 특수문자 포함 - 400 반환")
    void updateBootcamp_englishNameWithSpecialChars_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidEnglishNameRequest("wecode@pro!");

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} officialUrl 누락 - 400 반환")
    void updateBootcamp_blankOfficialUrl_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidOfficialUrlRequest("");

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} officialUrl 유효하지 않은 URL - 400 반환")
    void updateBootcamp_invalidOfficialUrl_returns400() throws Exception {
        var request = BootcampRequestFixture.invalidOfficialUrlRequest("not-a-url");

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} 존재하지 않는 ID - 404 반환")
    void updateBootcamp_notFound_returns404() throws Exception {
        var request = BootcampRequestFixture.updateRequest();

        mockMvc.perform(put("/api/v1/bootcamps/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ── 삭제 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /bootcamps/{id} - 204 삭제 성공")
    void deleteBootcamp_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/bootcamps/{id}", savedBootcamp.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /bootcamps/{id} 존재하지 않는 ID - 404 반환")
    void deleteBootcamp_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/bootcamps/{id}", 999L))
            .andExpect(status().isNotFound());
    }

    // ── 트랙 추가 ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks - 201 트랙 추가 성공")
    void addTrack_returns201() throws Exception {
        var request = BootcampTrackRequestFixture.createRequest();

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.trackType").value(request.trackType().name()));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks 필수값 누락 - 400 반환")
    void addTrack_missingRequired_returns400() throws Exception {
        var request = BootcampTrackRequestFixture.invalidRequest();

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks priceMin 음수 - 400 반환")
    void addTrack_negativePriceMin_returns400() throws Exception {
        var request = BootcampTrackRequestFixture.invalidPriceMinRequest();

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks priceMax 음수 - 400 반환")
    void addTrack_negativePriceMax_returns400() throws Exception {
        var request = BootcampTrackRequestFixture.invalidPriceMaxRequest();

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks priceMax < priceMin - 400 반환")
    void addTrack_priceMaxLessThanPriceMin_returns400() throws Exception {
        var request = BootcampTrackRequestFixture.invalidPriceRequest();

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks durationWeeks = 0 - 400 반환")
    void addTrack_durationWeeksZero_returns400() throws Exception {
        var request = BootcampTrackRequestFixture.invalidDurationWeeksRequest(0);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks durationWeeks > 104 - 400 반환")
    void addTrack_durationWeeksOver104_returns400() throws Exception {
        var request = BootcampTrackRequestFixture.invalidDurationWeeksRequest(105);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks price/duration null - 201 반환 (선택 필드 null 허용)")
    void addTrack_nullOptionalFields_returns201() throws Exception {
        var request = BootcampTrackRequestFixture.validDurationWeeksRequest(null);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.priceMin").value(request.priceMin()))
            .andExpect(jsonPath("$.data.priceMax").value(request.priceMax()))
            .andExpect(jsonPath("$.data.durationWeeks").isEmpty());
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /bootcamps/{id}/tracks/{trackId} - 200 트랙 수정 성공")
    void updateTrack_returns200() throws Exception {
        Long trackId = savedBootcamp.getTracks().getFirst().getId();
        var request = BootcampTrackRequestFixture.updateRequest();

        mockMvc.perform(put("/api/v1/bootcamps/{id}/tracks/{trackId}",
                savedBootcamp.getId(), trackId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.trackType").value(request.trackType().name()))
            .andExpect(jsonPath("$.data.operationType").value(request.operationType().name()));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id}/tracks/{trackId} priceMin 음수 - 400 반환")
    void updateTrack_negativePriceMin_returns400() throws Exception {
        Long trackId = savedBootcamp.getTracks().getFirst().getId();
        var request = BootcampTrackRequestFixture.invalidPriceMinRequest();

        mockMvc.perform(put("/api/v1/bootcamps/{id}/tracks/{trackId}",
                savedBootcamp.getId(), trackId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id}/tracks/{trackId} priceMax < priceMin - 400 반환")
    void updateTrack_priceMaxLessThanPriceMin_returns400() throws Exception {
        Long trackId = savedBootcamp.getTracks().getFirst().getId();
        var request = BootcampTrackRequestFixture.invalidPriceRequest();

        mockMvc.perform(put("/api/v1/bootcamps/{id}/tracks/{trackId}",
                savedBootcamp.getId(), trackId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ── 트랙 삭제 ─────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /bootcamps/{id}/tracks/{trackId} - 204 트랙 삭제 성공")
    void deleteTrack_returns204() throws Exception {
        Long bootcampId = savedBootcamp.getId();
        Long trackId = savedBootcamp.getTracks().getFirst().getId();

        mockMvc.perform(delete("/api/v1/bootcamps/{id}/tracks/{trackId}", bootcampId, trackId))
            .andExpect(status().isNoContent());
    }
}
