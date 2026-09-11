package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import java.util.Arrays;

import static Modules.Utils.InputParsing.parse;

public class VigenereCosetShift {
    private static final int TOP_GUESS_COUNT = 3;

    private static final double[] ENGLISH_FREQUENCIES = {
            8.167, // A
            1.492, // B
            2.782, // C
            4.253, // D
            12.702, // E
            2.228, // F
            2.015, // G
            6.094, // H
            6.966, // I
            0.153, // J
            0.772, // K
            4.025, // L
            2.406, // M
            6.749, // N
            7.507, // O
            1.929, // P
            0.095, // Q
            5.987, // R
            6.327, // S
            9.056, // T
            2.758, // U
            0.978, // V
            2.360, // W
            0.150, // X
            1.974, // Y
            0.074  // Z
    };

    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("keyLength", 'l', int.class, null),
                                new InputParsing.Argument("verbose", 'v', null, false) }
        );

        boolean verbose = (boolean) args[1].value;

        int keyLength = (int) args[0].value;
        if (keyLength < 1) {
            throw new IllegalArgumentException("Key length must be at least 1");
        }
        String filteredInput = filterInput(input);
        CosetShiftResult[] results = analyze(filteredInput, keyLength);

        if (verbose) {
            printVerboseResults(results);
        } else {
            printCompactResults(results);
        }

        return input;
    }

    private static CosetShiftResult[] analyze(String input, int keyLength) {
        CosetShiftResult[] results = new CosetShiftResult[keyLength];
        for (int cosetIndex = 0; cosetIndex < keyLength; cosetIndex++) {
            String coset = buildCoset(input, cosetIndex, keyLength);
            ShiftScore[] shiftScores = scoreShifts(coset);
            ShiftScore[] rankedShiftScores = shiftScores.clone();
            Arrays.sort(rankedShiftScores);

            results[cosetIndex] = new CosetShiftResult(cosetIndex + 1, coset.length(), shiftScores, rankedShiftScores);
        }

        return results;
    }

    private static void printCompactResults(CosetShiftResult[] results) {
        for (CosetShiftResult result : results) {
            System.out.print("Coset " + result.cosetNumber + " (length = " + result.cosetLength + "): ");

            for (int i = 0; i < TOP_GUESS_COUNT; i++) {
                if (i > 0) {
                    System.out.print(", ");
                }

                ShiftScore shiftScore = result.rankedShiftScores[i];
                System.out.printf(
                        "%c = %.4f",
                        shiftScore.keyLetter(),
                        shiftScore.chiSquared
                );
            }

            System.out.println();
        }

        System.out.println("Top key guesses:");
        for (int guessIndex = 0; guessIndex < TOP_GUESS_COUNT; guessIndex++) {
            System.out.print(guessLabel(guessIndex) + ": ");

            for (CosetShiftResult result : results) {
                System.out.print(result.rankedShiftScores[guessIndex].keyLetter());
            }

            System.out.println();
        }
    }

    private static String guessLabel(int guessIndex) {
        if (guessIndex == 0) {
            return "Best";
        }

        if (guessIndex == 1) {
            return " 2nd";
        }

        if (guessIndex == 2) {
            return " 3rd";
        }

        return " " + (guessIndex + 1) + "th";
    }

    private static void printVerboseResults(CosetShiftResult[] results) {
        for (CosetShiftResult result : results) {
            System.out.println("Coset " + result.cosetNumber + " (length = " + result.cosetLength + "):");
            for (ShiftScore shiftScore : result.shiftScores) {
                System.out.printf(
                        "  Shift %2d (%c): chi-squared = %.4f%n",
                        shiftScore.shiftAmount,
                        shiftScore.keyLetter(),
                        shiftScore.chiSquared
                );
            }
        }
    }

    private static ShiftScore[] scoreShifts(String coset) {
        ShiftScore[] shiftScores = new ShiftScore[26];

        for (int shiftAmount = 0; shiftAmount < 26; shiftAmount++) {
            int[] counts = countShiftedLetters(coset, shiftAmount);
            shiftScores[shiftAmount] = new ShiftScore(shiftAmount, calculateChiSquared(counts, coset.length()));
        }

        return shiftScores;
    }

    private static int[] countShiftedLetters(String input, int shiftAmount) {
        int[] counts = new int[26];

        for (char c : input.toCharArray()) {
            int letterIndex = Character.toUpperCase(c) - 'A';
            int shiftedIndex = Math.floorMod(letterIndex - shiftAmount, 26);
            counts[shiftedIndex]++;
        }

        return counts;
    }

    private static double calculateChiSquared(int[] counts, int totalLetters) {
        if (totalLetters == 0) {
            return 0;
        }

        double chiSquared = 0;
        for (int i = 0; i < counts.length; i++) {
            double expected = totalLetters * ENGLISH_FREQUENCIES[i] / 100;
            if (expected > 0) {
                double difference = counts[i] - expected;
                chiSquared += difference * difference / expected;
            }
        }

        return chiSquared;
    }

    private static String filterInput(String input) {
        StringBuilder filteredInput = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                filteredInput.append(c);
            }
        }

        return filteredInput.toString();
    }

    private static String buildCoset(String input, int cosetIndex, int keyLength) {
        StringBuilder coset = new StringBuilder();

        for (int i = cosetIndex; i < input.length(); i += keyLength) {
            coset.append(input.charAt(i));
        }

        return coset.toString();
    }

    private static class CosetShiftResult {
        final int cosetNumber;
        final int cosetLength;
        final ShiftScore[] shiftScores;
        final ShiftScore[] rankedShiftScores;

        CosetShiftResult(
                int cosetNumber,
                int cosetLength,
                ShiftScore[] shiftScores,
                ShiftScore[] rankedShiftScores
        ) {
            this.cosetNumber = cosetNumber;
            this.cosetLength = cosetLength;
            this.shiftScores = shiftScores;
            this.rankedShiftScores = rankedShiftScores;
        }
    }

    private static class ShiftScore implements Comparable<ShiftScore> {
        final int shiftAmount;
        final double chiSquared;

        ShiftScore(int shiftAmount, double chiSquared) {
            this.shiftAmount = shiftAmount;
            this.chiSquared = chiSquared;
        }

        char keyLetter() {
            return (char) ('A' + shiftAmount);
        }

        @Override
        public int compareTo(ShiftScore other) {
            return Double.compare(this.chiSquared, other.chiSquared);
        }
    }
}
