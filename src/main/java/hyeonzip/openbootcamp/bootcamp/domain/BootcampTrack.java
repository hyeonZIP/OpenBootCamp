package hyeonzip.openbootcamp.bootcamp.domain;

import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "bootcamp_tracks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BootcampTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @BatchSize(size = 100)
    private List<TechStack> techStacks = new ArrayList<>();

    private Integer priceMin;
    private Integer priceMax;
    private Integer durationWeeks;
    private Boolean isRecruiting;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private BootcampTrack(TrackType trackType, OperationType operationType, List<TechStack> techStacks,
                          Integer priceMin, Integer priceMax, Integer durationWeeks, Boolean isRecruiting) {
        this.trackType = trackType;
        this.operationType = operationType;
        this.techStacks = Optional.ofNullable(techStacks).orElseGet(ArrayList::new);
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
        this.techStacks = Optional.ofNullable(techStacks).orElseGet(ArrayList::new);
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.durationWeeks = durationWeeks;
        this.isRecruiting = isRecruiting;
    }
}
