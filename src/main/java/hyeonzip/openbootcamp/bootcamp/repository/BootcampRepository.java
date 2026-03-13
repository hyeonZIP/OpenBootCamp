package hyeonzip.openbootcamp.bootcamp.repository;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BootcampRepository extends JpaRepository<Bootcamp, Long> {

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query("SELECT b FROM Bootcamp b LEFT JOIN FETCH b.tracks WHERE b.id = :id")
    Optional<Bootcamp> findWithTracksById(@Param("id") Long id);

    @Query(value = """
            SELECT DISTINCT b FROM Bootcamp b
            WHERE (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:trackType IS NULL OR EXISTS (
                SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND bt.trackType = :trackType))
            AND (:operationType IS NULL OR EXISTS (
                SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND bt.operationType = :operationType))
            AND (:techStack IS NULL OR EXISTS (
                SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND :techStack MEMBER OF bt.techStacks))
            """,
            countQuery = """
            SELECT COUNT(DISTINCT b) FROM Bootcamp b
            WHERE (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:trackType IS NULL OR EXISTS (
                SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND bt.trackType = :trackType))
            AND (:operationType IS NULL OR EXISTS (
                SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND bt.operationType = :operationType))
            AND (:techStack IS NULL OR EXISTS (
                SELECT bt FROM BootcampTrack bt WHERE bt.bootcamp = b AND :techStack MEMBER OF bt.techStacks))
            """)
    Page<Bootcamp> findByFilters(
            @Param("keyword") String keyword,
            @Param("trackType") TrackType trackType,
            @Param("operationType") OperationType operationType,
            @Param("techStack") TechStack techStack,
            Pageable pageable);
}
