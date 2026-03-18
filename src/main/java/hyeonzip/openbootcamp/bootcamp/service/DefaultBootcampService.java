package hyeonzip.openbootcamp.bootcamp.service;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
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

        return bootcampRepository.findByFilters(keyword, trackType, operationType, techStack,
                pageable)
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

        validateBootcampNameDuplicate(request.name());

        Slug slug = Slug.from(request.englishName());

        validateBootcampSlugDuplicate(slug.getValue());

        Bootcamp bootcamp = Bootcamp.create(request.name(), slug, request.logoUrl(),
            request.description(), request.officialUrl());

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

        validateBootcampNameDuplicate(request.name(), id);

        Slug slug = Slug.from(request.englishName());

        validateBootcampSlugDuplicate(slug.getValue(), id);

        bootcamp.update(request.name(), slug, request.logoUrl(), request.description(),
            request.officialUrl());

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

        return BootcampTrackResponse.from(bootcampTrackRepository.save(track));
    }

    // ── 트랙 수정 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public BootcampTrackResponse updateTrack(Long bootcampId, Long trackId,
        BootcampTrackRequest request) {

        validateBootcampExisting(bootcampId);

        BootcampTrack track = bootcampTrackRepository.findByIdAndBootcampId(trackId, bootcampId)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.RESOURCE_NOT_FOUND));

        validatePriceRange(request.priceMin(), request.priceMax());

        track.update(request.trackType(), request.operationType(), request.techStacks(),
            request.priceMin(), request.priceMax(), request.durationWeeks(),
            request.isRecruiting());

        return BootcampTrackResponse.from(track);
    }

    // ── 트랙 삭제 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteTrack(Long bootcampId, Long trackId) {
        validateBootcampExisting(bootcampId);

        BootcampTrack track = bootcampTrackRepository.findByIdAndBootcampId(trackId, bootcampId)
            .orElseThrow(() -> new OpenBootCampException(ErrorCode.RESOURCE_NOT_FOUND));

        bootcampTrackRepository.delete(track);
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────────────

    private void validatePriceRange(Integer priceMin, Integer priceMax) {
        if (priceMin != null && priceMax != null && priceMax < priceMin) {
            throw new OpenBootCampException(ErrorCode.INVALID_INPUT);
        }
    }

    private BootcampTrack toEntity(BootcampTrackRequest req) {
        validatePriceRange(req.priceMin(), req.priceMax());

        return BootcampTrack.create(req.trackType(),
            req.operationType(),
            req.techStacks(),
            req.priceMin(),
            req.priceMax(),
            req.durationWeeks(),
            req.isRecruiting()
        );
    }

    private void validateBootcampNameDuplicate(String name) {
        if (bootcampRepository.existsByName(name)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_NAME_DUPLICATE);
        }
    }

    private void validateBootcampNameDuplicate(String name, Long excludeId) {
        if (bootcampRepository.existsByNameAndIdNot(name, excludeId)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_NAME_DUPLICATE);
        }
    }

    private void validateBootcampSlugDuplicate(String slug) {
        if (bootcampRepository.existsBySlugValue(slug)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_SLUG_DUPLICATE);
        }
    }

    private void validateBootcampSlugDuplicate(String slug, Long excludeId) {
        if (bootcampRepository.existsBySlugValueAndIdNot(slug, excludeId)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_SLUG_DUPLICATE);
        }
    }

    private void validateBootcampExisting(Long bootcampId) {
        if (!bootcampRepository.existsById(bootcampId)) {
            throw new OpenBootCampException(ErrorCode.BOOTCAMP_NOT_FOUND);
        }
    }
}
