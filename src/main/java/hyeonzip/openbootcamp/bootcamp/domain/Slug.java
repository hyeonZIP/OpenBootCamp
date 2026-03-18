package hyeonzip.openbootcamp.bootcamp.domain;

import hyeonzip.openbootcamp.common.exception.ErrorCode;
import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.text.Normalizer;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Embeddable
@Getter
public class Slug {

    private static final Pattern NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern LEADING_TRAILING_HYPHENS = Pattern.compile("^-+|-+$");

    @Column(name = "slug", unique = true)
    private String value;

    protected Slug() {
    }

    private Slug(String value) {
        this.value = value;
    }

    public static Slug from(String englishName) {
        if (StringUtils.hasText(englishName)) {
            String result = getNormalizedSlug(englishName);

            if (StringUtils.hasText(result)) {
                return new Slug(result);
            }
            throw new OpenBootCampException(ErrorCode.INVALID_INPUT,
                "유효하지 않은 영문명입니다. 영문자 또는 숫자를 포함해야 합니다: " + englishName);
        }
        throw new OpenBootCampException(ErrorCode.INVALID_INPUT,
            "영문명은 필수입니다.");
    }

    private static String getNormalizedSlug(String englishName) {
        String normalized = Normalizer.normalize(englishName, Normalizer.Form.NFD);
        String ascii = NON_ASCII.matcher(normalized).replaceAll("");
        String lower = ascii.toLowerCase().trim();
        String raw = NON_ALPHANUMERIC.matcher(lower).replaceAll("-");
        return LEADING_TRAILING_HYPHENS.matcher(raw).replaceAll("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Slug other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
