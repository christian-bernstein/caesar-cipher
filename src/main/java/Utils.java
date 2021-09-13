import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christian Bernstein
 */
public class Utils {

    @NonNull
    public static String border(@NonNull String text, @NonNull char[] font, @NonNull int[] margins) {
        final String[] lines = Arrays.stream(text.split("\n")).map(String::trim).toArray(String[]::new);
        final int maxLineLen = Arrays.stream(lines).mapToInt(String::length).max().orElse(0);
        final StringBuilder sb = new StringBuilder();
        // upper line
        sb.append(font[0]).append(String.valueOf(font[1]).repeat(margins[3] + maxLineLen + margins[1])).append(font[2]).append("\n");
        // upper margins
        for (int i = 0; i < margins[0]; i++) {
            sb.append(font[3]).append(" ".repeat(margins[3] + maxLineLen + margins[1])).append(font[7]).append("\n");
        }
        for (final String line : lines) {
            sb.append(font[3]).append(" ".repeat(margins[3])).append(line).append(" ".repeat((maxLineLen - line.length()) + margins[1])).append(font[7]).append("\n");
        }
        // lower margins
        for (int i = 0; i < margins[0]; i++) {
            sb.append(font[3]).append(" ".repeat(margins[3] + maxLineLen + margins[1])).append(font[7]).append("\n");
        }
        // lower line
        sb.append(font[6]).append(String.valueOf(font[5]).repeat(margins[3] + maxLineLen + margins[1])).append(font[4]);
        return sb.toString();
    }

    @NonNull
    public static String border(@NonNull String text, @NonNull String font, @NonNull List<Integer> margins) {
        return border(text, font.toCharArray(), margins.stream().mapToInt(i -> i).toArray());
    }

    @NonNull
    public static String border(@NonNull String text, @NonNull List<Integer> margins) {
        return border(text, "╔═╗║╝═╚║".toCharArray(), margins.stream().mapToInt(i -> i).toArray());
    }

    @NonNull
    public static String border(@NonNull String text, int margin) {
        return border(text, "╔═╗║╝═╚║".toCharArray(), new int[]{margin, margin, margin, margin});
    }

    @NonNull
    public static String border(@NonNull String text, int horizontalMargin, int verticalMargin) {
        return border(text, "╔═╗║╝═╚║".toCharArray(), new int[]{verticalMargin, horizontalMargin, verticalMargin, horizontalMargin});
    }
}
