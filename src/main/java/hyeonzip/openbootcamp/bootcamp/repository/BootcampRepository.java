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

    boolean existsBySlugValue(String slugValue);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsBySlugValueAndIdNot(String slugValue, Long id);

    @Query("SELECT DISTINCT b FROM Bootcamp b LEFT JOIN FETCH b.tracks WHERE b.id = :id")
    Optional<Bootcamp> findWithTracksById(@Param("id") Long id);

    @Query("SELECT DISTINCT b FROM Bootcamp b LEFT JOIN FETCH b.tracks WHERE b.slug.value = :slug")
    Optional<Bootcamp> findWithTracksBySlug(@Param("slug") String slug);

    @Query(value = """
        SELECT DISTINCT b
        FROM Bootcamp b
        LEFT JOIN b.tracks bt
        WHERE 1=1
        AND (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:trackType IS NULL OR bt.trackType = :trackType)
        AND (:operationType IS NULL OR bt.operationType = :operationType)
        AND (:techStack IS NULL OR :techStack MEMBER OF bt.techStacks)
        """,
        countQuery = """
        SELECT COUNT(DISTINCT b)
        FROM Bootcamp b
        LEFT JOIN b.tracks bt
        WHERE 1=1
        AND (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:trackType IS NULL OR bt.trackType = :trackType)
        AND (:operationType IS NULL OR bt.operationType = :operationType)
        AND (:techStack IS NULL OR :techStack MEMBER OF bt.techStacks)
        """)
    Page<Bootcamp> findByFilters(
        @Param("keyword") String keyword,
        @Param("trackType") TrackType trackType,
        @Param("operationType") OperationType operationType,
        @Param("techStack") TechStack techStack,
        Pageable pageable);
}
