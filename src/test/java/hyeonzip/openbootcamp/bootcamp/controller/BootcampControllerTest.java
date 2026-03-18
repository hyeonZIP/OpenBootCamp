package hyeonzip.openbootcamp.bootcamp.controller;

import tools.jackson.databind.ObjectMapper;
import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackRequest;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TrackType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @PersistenceContext
    private EntityManager entityManager;

    private Bootcamp savedBootcamp;

    @BeforeEach
    void setUp() {
        savedBootcamp = bootcampRepository.save(
                Bootcamp.builder()
                        .name("Wecode")
                        .slug(Slug.from("wecode"))
                        .description("백엔드 부트캠프")
                        .officialUrl("https://wecode.co.kr")
                        .build()
        );
        savedBootcamp.addTrack(
                BootcampTrack.builder()
                        .trackType(TrackType.BACKEND)
                        .operationType(OperationType.ONLINE)
                        .priceMin(150)
                        .priceMax(200)
                        .durationWeeks(12)
                        .isRecruiting(true)
                        .build()
        );
        bootcampRepository.flush();
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
        mockMvc.perform(get("/api/v1/bootcamps").param("keyword", "Wecode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Wecode"));
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
                .andExpect(jsonPath("$.data.name").value("Wecode"))
                .andExpect(jsonPath("$.data.tracks").isArray())
                .andExpect(jsonPath("$.data.tracks[0].trackType").value("BACKEND"));
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
        mockMvc.perform(get("/api/v1/bootcamps/slug/{slug}", "wecode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Wecode"))
                .andExpect(jsonPath("$.data.slug").value("wecode"))
                .andExpect(jsonPath("$.data.tracks").isArray())
                .andExpect(jsonPath("$.data.tracks[0].trackType").value("BACKEND"));
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
        BootcampTrackRequest trackRequest = new BootcampTrackRequest(
                TrackType.FRONTEND, OperationType.HYBRID, null, 100, 150, 8, true);
        BootcampRequest request = new BootcampRequest(
                "코드스테이츠", "Codestates", null, "풀스택 부트캠프", null, List.of(trackRequest));

        mockMvc.perform(post("/api/v1/bootcamps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("코드스테이츠"))
                .andExpect(jsonPath("$.data.slug").value("codestates"))
                .andExpect(jsonPath("$.data.tracks[0].trackType").value("FRONTEND"));
    }

    @Test
    @DisplayName("POST /bootcamps 이름 누락 - 400 반환")
    void createBootcamp_blankName_returns400() throws Exception {
        BootcampRequest request = new BootcampRequest("", "some-name", null, null, null, null);

        mockMvc.perform(post("/api/v1/bootcamps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps 중복 이름 - 409 반환")
    void createBootcamp_duplicateName_returns409() throws Exception {
        BootcampRequest request = new BootcampRequest("Wecode", "wecode-unique", null, null, null, null);

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
        BootcampRequest request = new BootcampRequest(
                "위코드 Pro", "Wecode Pro", null, "업데이트된 설명", null, null);

        mockMvc.perform(put("/api/v1/bootcamps/{id}", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("위코드 Pro"))
                .andExpect(jsonPath("$.data.slug").value("wecode-pro"));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id} 존재하지 않는 ID - 404 반환")
    void updateBootcamp_notFound_returns404() throws Exception {
        BootcampRequest request = new BootcampRequest("New Name", "new-name", null, null, null, null);

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
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.FULLSTACK, OperationType.OFFLINE, null, 200, 300, 16, false);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.trackType").value("FULLSTACK"));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks 필수값 누락 - 400 반환")
    void addTrack_missingRequired_returns400() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                null, OperationType.ONLINE, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks priceMin 음수 - 400 반환")
    void addTrack_negativePriceMin_returns400() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, -1, 100, 12, true);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks priceMax 음수 - 400 반환")
    void addTrack_negativePriceMax_returns400() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, 100, -1, 12, true);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks priceMax < priceMin - 400 반환")
    void addTrack_priceMaxLessThanPriceMin_returns400() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, 200, 100, 12, true);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks durationWeeks = 0 - 400 반환")
    void addTrack_durationWeeksZero_returns400() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, null, null, 0, true);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks durationWeeks > 104 - 400 반환")
    void addTrack_durationWeeksOver104_returns400() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, null, null, 105, true);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /bootcamps/{id}/tracks price/duration null - 201 반환 (선택 필드 null 허용)")
    void addTrack_nullOptionalFields_returns201() throws Exception {
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.DATA, OperationType.ONLINE, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/bootcamps/{id}/tracks", savedBootcamp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.priceMin").isEmpty())
                .andExpect(jsonPath("$.data.priceMax").isEmpty())
                .andExpect(jsonPath("$.data.durationWeeks").isEmpty());
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /bootcamps/{id}/tracks/{trackId} - 200 트랙 수정 성공")
    void updateTrack_returns200() throws Exception {
        Long trackId = savedBootcamp.getTracks().get(0).getId();
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.FRONTEND, OperationType.OFFLINE, null, 100, 150, 8, false);

        mockMvc.perform(put("/api/v1/bootcamps/{id}/tracks/{trackId}",
                        savedBootcamp.getId(), trackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.trackType").value("FRONTEND"))
                .andExpect(jsonPath("$.data.operationType").value("OFFLINE"));
    }

    @Test
    @DisplayName("PUT /bootcamps/{id}/tracks/{trackId} priceMin 음수 - 400 반환")
    void updateTrack_negativePriceMin_returns400() throws Exception {
        Long trackId = savedBootcamp.getTracks().get(0).getId();
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, -10, 100, 12, true);

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
        Long trackId = savedBootcamp.getTracks().get(0).getId();
        BootcampTrackRequest request = new BootcampTrackRequest(
                TrackType.BACKEND, OperationType.ONLINE, null, 300, 100, 12, true);

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
        Long trackId = savedBootcamp.getTracks().get(0).getId();
        entityManager.clear();

        mockMvc.perform(delete("/api/v1/bootcamps/{id}/tracks/{trackId}", bootcampId, trackId))
                .andExpect(status().isNoContent());
    }
}
