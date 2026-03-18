package hyeonzip.openbootcamp.bootcamp.domain;

import hyeonzip.openbootcamp.common.entity.AbstractEntity;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bootcamp_tracks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BootcampTrack extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bootcamp_id", nullable = false)
    private Bootcamp bootcamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackType trackType;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @ElementCollection
    @CollectionTable(
        name = "bootcamp_track_tech_stacks",
        joinColumns = @JoinColumn(name = "bootcamp_track_id")
    )
    @Column(name = "tech_stack")
    @Enumerated(EnumType.STRING)
    private List<TechStack> techStacks = new ArrayList<>();

    private Integer priceMin;
    private Integer priceMax;
    private Integer durationWeeks;
    private Boolean isRecruiting;

    @Builder
    private BootcampTrack(TrackType trackType, OperationType operationType,
        List<TechStack> techStacks,
        Integer priceMin, Integer priceMax, Integer durationWeeks, Boolean isRecruiting) {
        this.trackType = trackType;
        this.operationType = operationType;
        this.techStacks = Optional.ofNullable(techStacks).map(ArrayList::new)
            .orElseGet(ArrayList::new);
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.durationWeeks = durationWeeks;
        this.isRecruiting = isRecruiting;
    }

    void assignBootcamp(Bootcamp bootcamp) {
        this.bootcamp = bootcamp;
    }

    public void update(TrackType trackType, OperationType operationType, List<TechStack> techStacks,
        Integer priceMin, Integer priceMax, Integer durationWeeks, Boolean isRecruiting) {
        this.trackType = trackType;
        this.operationType = operationType;
        this.techStacks = Optional.ofNullable(techStacks).map(ArrayList::new)
            .orElseGet(ArrayList::new);
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.durationWeeks = durationWeeks;
        this.isRecruiting = isRecruiting;
    }
}
