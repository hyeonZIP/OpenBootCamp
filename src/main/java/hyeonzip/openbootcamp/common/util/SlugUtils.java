package hyeonzip.openbootcamp.common.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NON_ASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern LEADING_TRAILING_HYPHENS = Pattern.compile("^-|-$");

    private SlugUtils() {}

    public static String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String ascii = NON_ASCII.matcher(normalized).replaceAll("");
        String lower = ascii.toLowerCase().trim();
        String slug = NON_ALPHANUMERIC.matcher(lower).replaceAll("-");
        return LEADING_TRAILING_HYPHENS.matcher(slug).replaceAll("");
    }
}
