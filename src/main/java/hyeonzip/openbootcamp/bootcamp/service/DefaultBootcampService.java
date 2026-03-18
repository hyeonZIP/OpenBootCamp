package hyeonzip.openbootcamp.bootcamp.service;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampResponse;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackRequest;
import hyeonzip.openbootcamp.bootcamp.dto.BootcampTrackResponse;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampTrackRepository;
import hyeonzip.openbootcamp.bootcamp.service.ports.inp.BootcampService;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import hyeonzip.openbootcamp.common.util.SlugUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultBootcampService implements BootcampService {

    private final BootcampRepository bootcampRepository;
    private final BootcampTrackRepository bootcampTrackRepository;

    // ── 목록 조회 ──────────────────────────────────────────────────

    @Override
    public Page<BootcampResponse> getBootcamps(
            TrackType trackType, OperationType operationType,
            TechStack techStack, String keyword, Pageable pageable) {
        return bootcampRepository.findByFilters(keyword, trackType, operationType, techStack, pageable)
                .map(BootcampResponse::from);
    }

    // ── 단건 조회 ──────────────────────────────────────────────────

    @Override
    public BootcampResponse getBootcamp(Long id) {
        Bootcamp bootcamp = bootcampRepository.findWithTracksById(id)
                .orElseThrow(() -> new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND));
        return BootcampResponse.from(bootcamp);
    }

    @Override
    public BootcampResponse getBootcampBySlug(String slug) {
        Bootcamp bootcamp = bootcampRepository.findWithTracksBySlug(slug)
                .orElseThrow(() -> new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND));
        return BootcampResponse.from(bootcamp);
    }

    // ── 등록 ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public BootcampResponse createBootcamp(BootcampRequest request) {
        if (bootcampRepository.existsByName(request.name())) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_SLUG_DUPLICATE,
                    "이미 존재하는 부트캠프 이름입니다: " + request.name());
        }

        String slug = generateUniqueSlug(request.name(), null);

        Bootcamp bootcamp = Bootcamp.builder()
                .name(request.name())
                .slug(slug)
                .logoUrl(request.logoUrl())
                .description(request.description())
                .officialUrl(request.officialUrl())
                .build();

        if (request.tracks() != null) {
            request.tracks().forEach(trackReq -> bootcamp.addTrack(toEntity(trackReq)));
        }

        return BootcampResponse.from(bootcampRepository.save(bootcamp));
    }

    // ── 수정 ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public BootcampResponse updateBootcamp(Long id, BootcampRequest request) {
        Bootcamp bootcamp = bootcampRepository.findWithTracksById(id)
                .orElseThrow(() -> new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND));

        if (bootcampRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_SLUG_DUPLICATE,
                    "이미 존재하는 부트캠프 이름입니다: " + request.name());
        }

        String slug = generateUniqueSlug(request.name(), id);
        bootcamp.update(request.name(), slug, request.logoUrl(), request.description(), request.officialUrl());

        return BootcampResponse.from(bootcamp);
    }

    // ── 삭제 ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteBootcamp(Long id) {
        if (!bootcampRepository.existsById(id)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND);
        }
        bootcampRepository.deleteById(id);
    }

    // ── 트랙 추가 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public BootcampTrackResponse addTrack(Long bootcampId, BootcampTrackRequest request) {
        Bootcamp bootcamp = bootcampRepository.findWithTracksById(bootcampId)
                .orElseThrow(() -> new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND));

        BootcampTrack track = toEntity(request);
        bootcamp.addTrack(track);
        bootcampRepository.flush();

        return BootcampTrackResponse.from(track);
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public BootcampTrackResponse updateTrack(Long bootcampId, Long trackId, BootcampTrackRequest request) {
        if (!bootcampRepository.existsById(bootcampId)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND);
        }
        BootcampTrack track = bootcampTrackRepository.findByIdAndBootcampId(trackId, bootcampId)
                .orElseThrow(() -> new OpenBootCampException(ErrorCode.RESOURCE_NOT_FOUND,
                        "해당 부트캠프의 트랙을 찾을 수 없습니다."));

        validatePriceRange(request.priceMin(), request.priceMax());
        track.update(request.trackType(), request.operationType(), request.techStacks(),
                request.priceMin(), request.priceMax(), request.durationWeeks(), request.isRecruiting());

        return BootcampTrackResponse.from(track);
    }

    // ── 트랙 삭제 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteTrack(Long bootcampId, Long trackId) {
        if (!bootcampRepository.existsById(bootcampId)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND);
        }
        BootcampTrack track = bootcampTrackRepository.findByIdAndBootcampId(trackId, bootcampId)
                .orElseThrow(() -> new OpenBootCampException(ErrorCode.RESOURCE_NOT_FOUND,
                        "해당 부트캠프의 트랙을 찾을 수 없습니다."));

        bootcampTrackRepository.delete(track);
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────────────

    private String generateUniqueSlug(String name, Long excludeId) {
        String base = SlugUtils.toSlug(name);
        String slug = base;
        int count = 1;

        while (true) {
            boolean exists;
            if (excludeId == null) {
                exists = bootcampRepository.existsBySlug(slug);
            } else {
                exists = bootcampRepository.existsBySlugAndIdNot(slug, excludeId);
            }
            if (!exists) break;
            slug = base + "-" + count++;
        }
        return slug;
    }

    private void validatePriceRange(Integer priceMin, Integer priceMax) {
        if (priceMin != null && priceMax != null && priceMax < priceMin) {
            throw new OpenBootCampException(ErrorCode.INVALID_INPUT,
                    "수강료 최댓값은 최솟값 이상이어야 합니다.");
        }
    }

    private BootcampTrack toEntity(BootcampTrackRequest req) {
        validatePriceRange(req.priceMin(), req.priceMax());

        List<TechStack> techStacks = Optional.ofNullable(req.techStacks()).orElseGet(ArrayList::new);
        return BootcampTrack.builder()
                .trackType(req.trackType())
                .operationType(req.operationType())
                .techStacks(techStacks)
                .priceMin(req.priceMin())
                .priceMax(req.priceMax())
                .durationWeeks(req.durationWeeks())
                .isRecruiting(req.isRecruiting())
                .build();
    }
}
