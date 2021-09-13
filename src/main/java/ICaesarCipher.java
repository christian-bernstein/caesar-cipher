import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * @author Christian Bernstein
 */
public interface ICaesarCipher {

    @NonNull String dataCleanup(@NonNull final String message);

    @NotNull String cipher(@NonNull final String message, final int offset);

    @NotNull String decipher(@NonNull final String message, final int offset);

    double @NotNull [] createChiSquares(@NonNull final String ciphered);

    int findMostProbableOffset(final double @NotNull [] chiSquares);

    long[] countLetterFrequencies(@NonNull final String message);

    long countLetter(@NotNull final String message, final char letter);

    double @NonNull [] lettersProbabilities();
}
