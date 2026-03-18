package hyeonzip.openbootcamp.bootcamp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hyeonzip.openbootcamp.common.exception.OpenBootCampException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SlugTest {

    // ── 정상 생성 ──────────────────────────────────────────────────

    @Test
    @DisplayName("영문 소문자는 그대로 slug가 된다")
    void from_lowercase_returnsAsIs() {
        assertThat(Slug.from("wecode").getValue()).isEqualTo("wecode");
    }

    @Test
    @DisplayName("영문 대문자는 소문자로 변환된다")
    void from_uppercase_convertsToLowercase() {
        assertThat(Slug.from("Wecode").getValue()).isEqualTo("wecode");
        assertThat(Slug.from("CODESTATES").getValue()).isEqualTo("codestates");
    }

    @Test
    @DisplayName("공백은 하이픈으로 변환된다")
    void from_spaces_replacedWithHyphens() {
        assertThat(Slug.from("wecode pro").getValue()).isEqualTo("wecode-pro");
    }

    @Test
    @DisplayName("연속 공백 및 특수문자는 단일 하이픈으로 합쳐진다")
    void from_consecutiveNonAlphanumeric_collapsedToSingleHyphen() {
        assertThat(Slug.from("wecode  pro").getValue()).isEqualTo("wecode-pro");
        assertThat(Slug.from("wecode---pro").getValue()).isEqualTo("wecode-pro");
    }

    @Test
    @DisplayName("앞뒤 하이픈은 제거된다")
    void from_leadingTrailingHyphens_removed() {
        assertThat(Slug.from("-wecode-").getValue()).isEqualTo("wecode");
    }

    @Test
    @DisplayName("숫자를 포함한 영문명도 정상 처리된다")
    void from_alphanumeric_handledCorrectly() {
        assertThat(Slug.from("hanghae99").getValue()).isEqualTo("hanghae99");
        assertThat(Slug.from("boot2024camp").getValue()).isEqualTo("boot2024camp");
    }

    @Test
    @DisplayName("앞뒤 공백은 제거된다")
    void from_trimsPadding() {
        assertThat(Slug.from("  wecode  ").getValue()).isEqualTo("wecode");
    }

    // ── 예외 케이스 ──────────────────────────────────────────────────

    @Test
    @DisplayName("null 입력 시 OpenBootCampException이 발생한다")
    void from_null_throwsException() {
        assertThatThrownBy(() -> Slug.from(null))
            .isInstanceOf(OpenBootCampException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("빈 문자열 또는 공백만 입력 시 OpenBootCampException이 발생한다")
    void from_blankInput_throwsException(String input) {
        assertThatThrownBy(() -> Slug.from(input))
            .isInstanceOf(OpenBootCampException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"위코드", "코드스테이츠", "항해가나다"})
    @DisplayName("한글만 입력 시 ASCII 변환 후 빈 결과가 되어 OpenBootCampException이 발생한다")
    void from_koreanOnly_throwsException(String input) {
        assertThatThrownBy(() -> Slug.from(input))
            .isInstanceOf(OpenBootCampException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"---", "!!!", "@#$"})
    @DisplayName("특수문자만 입력 시 OpenBootCampException이 발생한다")
    void from_specialCharsOnly_throwsException(String input) {
        assertThatThrownBy(() -> Slug.from(input))
            .isInstanceOf(OpenBootCampException.class);
    }

    // ── equals / hashCode ─────────────────────────────────────────

    @Test
    @DisplayName("동일한 값을 가진 두 Slug는 equal하다")
    void equals_sameValue_returnsTrue() {
        Slug a = Slug.from("wecode");
        Slug b = Slug.from("wecode");

        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("다른 값을 가진 두 Slug는 equal하지 않다")
    void equals_differentValue_returnsFalse() {
        assertThat(Slug.from("wecode")).isNotEqualTo(Slug.from("codestates"));
    }

    @Test
    @DisplayName("동일한 값의 두 Slug는 hashCode가 같다")
    void hashCode_sameValue_sameHashCode() {
        assertThat(Slug.from("wecode").hashCode()).isEqualTo(Slug.from("wecode").hashCode());
    }

    // ── toString ─────────────────────────────────────────────────

    @Test
    @DisplayName("toString은 slug 문자열 값을 반환한다")
    void toString_returnsValue() {
        assertThat(Slug.from("wecode").toString()).isEqualTo("wecode");
    }
}
