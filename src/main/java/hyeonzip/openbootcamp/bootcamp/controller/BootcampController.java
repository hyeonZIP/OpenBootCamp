package hyeonzip.openbootcamp.bootcamp.controller;

import hyeonzip.openbootcamp.bootcamp.dto.*;
import hyeonzip.openbootcamp.bootcamp.service.ports.inp.BootcampService;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import hyeonzip.openbootcamp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bootcamps")
@RequiredArgsConstructor
public class BootcampController {

    private final BootcampService bootcampService;

    // ── 목록 조회 ──────────────────────────────────────────────────
    @GetMapping
    public ApiResponse<Page<BootcampResponse>> getBootcamps(
            @RequestParam(required = false) TrackType trackType,
            @RequestParam(required = false) OperationType operationType,
            @RequestParam(required = false) TechStack techStack,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.ok(bootcampService.getBootcamps(trackType, operationType, techStack, keyword, pageable));
    }

    // ── 단건 조회 ──────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ApiResponse<BootcampResponse> getBootcamp(@PathVariable Long id) {
        return ApiResponse.ok(bootcampService.getBootcamp(id));
    }

    // ── 등록 ──────────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BootcampResponse> createBootcamp(@RequestBody @Valid BootcampRequest request) {
        return ApiResponse.ok(bootcampService.createBootcamp(request));
    }

    // ── 수정 ──────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ApiResponse<BootcampResponse> updateBootcamp(
            @PathVariable Long id,
            @RequestBody @Valid BootcampRequest request
    ) {
        return ApiResponse.ok(bootcampService.updateBootcamp(id, request));
    }

    // ── 삭제 ──────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBootcamp(@PathVariable Long id) {
        bootcampService.deleteBootcamp(id);
    }

    // ── 트랙 추가 ─────────────────────────────────────────────────
    @PostMapping("/{id}/tracks")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BootcampTrackResponse> addTrack(
            @PathVariable Long id,
            @RequestBody @Valid BootcampTrackRequest request
    ) {
        return ApiResponse.ok(bootcampService.addTrack(id, request));
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────
    @PutMapping("/{id}/tracks/{trackId}")
    public ApiResponse<BootcampTrackResponse> updateTrack(
            @PathVariable Long id,
            @PathVariable Long trackId,
            @RequestBody @Valid BootcampTrackRequest request
    ) {
        return ApiResponse.ok(bootcampService.updateTrack(id, trackId, request));
    }

    // ── 트랙 삭제 ─────────────────────────────────────────────────
    @DeleteMapping("/{id}/tracks/{trackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrack(@PathVariable Long id, @PathVariable Long trackId) {
        bootcampService.deleteTrack(id, trackId);
    }
}
