/*
 * Copyright (C) 2021 Christian Bernstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static lombok.Builder.Default;

/**
 * @author Christian Bernstein
 */
@Getter
@Builder
@Accessors(fluent = true)
public class CaesarCipher implements ICaesarCipher {

    public static final double[] englishLettersProbabilities = {
            0.073, 0.009, 0.030, // ABC
            0.044, 0.130, 0.028, // DEF
            0.016, 0.035, 0.074, // GHI
            0.002, 0.003, 0.035, // JKL
            0.025, 0.078, 0.074, // MNO
            0.027, 0.003, 0.077, // PQR
            0.063, 0.093, 0.027, // STU
            0.013, 0.016, 0.005, // VWX
            0.019, 0.001         // YZ
    };

    public static final double[] germanLettersProbabilities = {
            0.0558, 0.0196, 0.0316,
            0.0498, 0.1693, 0.0149,
            0.0302, 0.0498, 0.0802,
            0.0024, 0.0132, 0.0360,
            0.0255, 0.1053, 0.0224,
            0.0067, 0.0002, 0.0689,
            0.0642, 0.0579, 0.0383,
            0.0084, 0.0178, 0.0005,
            0.0005, 0.0121
    };
    @Default
    private double[] lettersProbabilities = CaesarCipher.englishLettersProbabilities;

    @Override
    public @NonNull String dataCleanup(@NonNull String message) {
        return message.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", "");
    }

    @Override
    public @NotNull String cipher(@NonNull final String message, final int offset) {
        StringBuilder result = new StringBuilder();
        for (char character : message.toCharArray()) {
            if (character != ' ') {
                int originalAlphabetPosition = character - 'a';
                int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
                char newCharacter = (char) ('a' + newAlphabetPosition);
                result.append(newCharacter);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    @Override
    public @NotNull String decipher(@NonNull final String message, final int offset) {
        return cipher(message, 26 - (offset % 26));
    }

    @Override
    public double @NotNull [] createChiSquares(@NonNull final String ciphered) {
        final double[] expectedLettersFrequencies = Arrays.stream(englishLettersProbabilities)
                .map(probability -> probability * ciphered.length())
                .toArray();
        final double[] chiSquares = new double[26];
        for (int offset = 0; offset < chiSquares.length; offset++) {
            String decipheredMessage = decipher(ciphered, offset);
            long[] lettersFrequencies = countLetterFrequencies(decipheredMessage);
            double chiSquare = new ChiSquareTest().chiSquare(expectedLettersFrequencies, lettersFrequencies);
            chiSquares[offset] = chiSquare;
        }
        return chiSquares;
    }

    @Override
    @Contract(pure = true)
    public int findMostProbableOffset(final double @NotNull [] chiSquares) {
        int probableOffset = 0;
        for (int offset = 0; offset < chiSquares.length; offset++) {
            // System.out.println(String.format("Chi-Square for offset %d: %.2f", offset, chiSquares[offset]));
            if (chiSquares[offset] < chiSquares[probableOffset]) {
                probableOffset = offset;
            }
        }

        return probableOffset;
    }

    @Override
    public long[] countLetterFrequencies(@NonNull final String message) {
        return IntStream.rangeClosed('a', 'z')
                .mapToLong(letter -> countLetter(message, (char) letter))
                .toArray();
    }

    @Override
    public long countLetter(@NotNull final String message, final char letter) {
        return message.chars()
                .filter(character -> character == letter)
                .count();
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public static void main(String[] args) {
        final ICaesarCipher cipher = CaesarCipher.builder()
                .lettersProbabilities(CaesarCipher.germanLettersProbabilities)
                .build();

        final String original = cipher.dataCleanup("Hallo, Ich bin Christian aus Deutschland und meine Arbeit ist wunderbar");
        final int expectedOffset = ThreadLocalRandom.current().nextInt(26);
        final String ciphered = cipher.cipher(original, expectedOffset);
        final int prob = cipher.findMostProbableOffset(cipher.createChiSquares(ciphered));

        final StringBuilder sb = new StringBuilder()
                .append(String.format("original: '%s'", original)).append("\n").append("↓\n")
                .append(String.format("offset: %d", expectedOffset)).append("\n").append("↓\n")
                .append(String.format("ciphered: %s", ciphered)).append("\n").append("↓\n")
                .append(String.format("statistic: %d", prob)).append("\n").append("↓\n")
                .append(String.format("deciphered: %s", cipher.decipher(ciphered, prob)))
        ;
        System.out.println(Utils.border(sb.toString(), 2, 1));
    }
}
