package hyeonzip.openbootcamp.bootcamp.domain;

import hyeonzip.openbootcamp.common.entity.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bootcamps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bootcamp extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Embedded
    private Slug slug;

    @Column(unique = true, nullable = false)
    private String englishName;

    private String logoUrl;

    @Column(length = 500)
    private String description;

    private String officialUrl;

    @OneToMany(mappedBy = "bootcamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BootcampTrack> tracks = new ArrayList<>();

    @Builder
    private Bootcamp(String name, Slug slug, String englishName, String logoUrl, String description,
        String officialUrl) {
        this.name = name;
        this.slug = slug;
        this.englishName = englishName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.officialUrl = officialUrl;
    }

    public static Bootcamp create(String name, Slug slug, String englishName, String logoUrl,
        String description, String officialUrl) {
        return Bootcamp.builder()
            .name(name)
            .slug(slug)
            .englishName(englishName)
            .logoUrl(logoUrl)
            .description(description)
            .officialUrl(officialUrl)
            .build();
    }

    public void update(String name, Slug slug, String englishName, String logoUrl,
        String description, String officialUrl) {
        this.name = name;
        this.slug = slug;
        this.englishName = englishName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.officialUrl = officialUrl;
    }

    public void addTrack(BootcampTrack track) {
        tracks.add(track);
        track.assignBootcamp(this);
    }
}
