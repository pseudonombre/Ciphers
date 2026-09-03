package Modules;

import java.util.ArrayList;
import java.util.List;

public class CosetIC {
    private static final int DEFAULT_MIN_KEY_LENGTH = 1;
    private static final int DEFAULT_MAX_KEY_LENGTH = 16;
    private static final String RESET_COLOR = "\u001B[0m";
    private static final String[] COLORS = {
            "\u001B[34m", // blue
            "\u001B[36m", // cyan
            "\u001B[96m", // bright cyan
            "\u001B[32m", // green
            "\u001B[92m", // bright green
            "\u001B[33m", // yellow
            "\u001B[93m", // bright yellow
            "\u001B[35m", // magenta
            "\u001B[95m", // bright magenta
            "\u001B[31m"  // red
    };

    public static String function(String input, String[] moduleArgs) {
        int minKeyLength = DEFAULT_MIN_KEY_LENGTH;
        int maxKeyLength = DEFAULT_MAX_KEY_LENGTH;
        boolean verbose = false;
        int keyLengthArgCount = moduleArgs.length;

        if (moduleArgs.length > 0 && isBooleanArg(moduleArgs[moduleArgs.length - 1])) {
            verbose = parseBooleanArg(moduleArgs[moduleArgs.length - 1]);
            keyLengthArgCount--;
        }

        if (keyLengthArgCount == 1) {
            minKeyLength = parseKeyLength(moduleArgs[0]);
            maxKeyLength = minKeyLength;
        } else if (keyLengthArgCount == 2) {
            minKeyLength = parseKeyLength(moduleArgs[0]);
            maxKeyLength = parseKeyLength(moduleArgs[1]);
        } else if (keyLengthArgCount > 2) {
            throw new IllegalArgumentException("Usage: cosetic [keyLength | minKeyLength maxKeyLength] [verbose]");
        }

        if (minKeyLength > maxKeyLength) {
            int temp = minKeyLength;
            minKeyLength = maxKeyLength;
            maxKeyLength = temp;
        }

        String filteredInput = filterInput(input);
        CosetICAnalysis analysis = analyze(filteredInput, minKeyLength, maxKeyLength);
        printResults(analysis, verbose);

        return input;
    }

    private static CosetICAnalysis analyze(String input, int minKeyLength, int maxKeyLength) {
        List<KeyLengthResult> results = new ArrayList<>();
        double minAverageIc = Double.POSITIVE_INFINITY;
        double maxAverageIc = Double.NEGATIVE_INFINITY;
        double minCosetIc = Double.POSITIVE_INFINITY;
        double maxCosetIc = Double.NEGATIVE_INFINITY;

        for (int keyLength = minKeyLength; keyLength <= maxKeyLength; keyLength++) {
            KeyLengthResult result = analyzeKeyLength(input, keyLength);
            results.add(result);

            minAverageIc = Math.min(minAverageIc, result.averageIc);
            maxAverageIc = Math.max(maxAverageIc, result.averageIc);

            for (double cosetIc : result.cosetIcs) {
                minCosetIc = Math.min(minCosetIc, cosetIc);
                maxCosetIc = Math.max(maxCosetIc, cosetIc);
            }
        }

        return new CosetICAnalysis(
                results,
                minAverageIc,
                maxAverageIc,
                minCosetIc,
                maxCosetIc
        );
    }

    private static void printResults(CosetICAnalysis analysis, boolean verbose) {
        for (KeyLengthResult result : analysis.results) {
            if (verbose) {
                printVerboseResult(result, analysis);
            } else {
                printAverageResult(result, analysis);
            }
        }
    }

    private static void printAverageResult(KeyLengthResult result, CosetICAnalysis analysis) {
        System.out.printf(
                "Key length %d: Average IC = %s%n",
                result.keyLength,
                colorize(formatIc(result.averageIc), result.averageIc,
                        analysis.minAverageIc, analysis.maxAverageIc)
        );
    }

    private static void printVerboseResult(KeyLengthResult result, CosetICAnalysis analysis) {
        System.out.println("Key length " + result.keyLength + " (longest coset length = "
                + result.longestCosetLength + "):");
        System.out.println("  Average IC = " + colorize(formatIc(result.averageIc), result.averageIc,
                analysis.minAverageIc, analysis.maxAverageIc));

        for (int i = 0; i < result.cosetIcs.length; i++) {
            String ic = colorize(formatIc(result.cosetIcs[i]), result.cosetIcs[i],
                    analysis.minCosetIc, analysis.maxCosetIc);
            System.out.printf(
                    "  Coset %d: IC = %s, length = %d%n",
                    i + 1,
                    ic,
                    result.cosetLengths[i]
            );
        }
    }

    private static KeyLengthResult analyzeKeyLength(String input, int keyLength) {
        double[] cosetIcs = new double[keyLength];
        int[] cosetLengths = new int[keyLength];
        int longestCosetLength = 0;
        double totalIc = 0;

        for (int cosetIndex = 0; cosetIndex < keyLength; cosetIndex++) {
            String coset = buildCoset(input, cosetIndex, keyLength);
            double indexOfCoincidence = IC.calculate(coset);

            cosetIcs[cosetIndex] = indexOfCoincidence;
            cosetLengths[cosetIndex] = coset.length();
            longestCosetLength = Math.max(longestCosetLength, coset.length());
            totalIc += indexOfCoincidence;
        }

        return new KeyLengthResult(
                keyLength,
                cosetIcs,
                cosetLengths,
                totalIc / keyLength,
                longestCosetLength
        );
    }

    private static int parseKeyLength(String value) {
        int keyLength = Integer.parseInt(value);
        if (keyLength < 1) {
            throw new IllegalArgumentException("Key length must be at least 1");
        }
        return keyLength;
    }

    private static boolean isBooleanArg(String value) {
        String normalizedValue = value.toLowerCase();
        return normalizedValue.equals("true")
                || normalizedValue.equals("t")
                || normalizedValue.equals("yes")
                || normalizedValue.equals("y")
                || normalizedValue.equals("false")
                || normalizedValue.equals("f")
                || normalizedValue.equals("no")
                || normalizedValue.equals("n");
    }

    private static boolean parseBooleanArg(String value) {
        String normalizedValue = value.toLowerCase();
        return normalizedValue.equals("true")
                || normalizedValue.equals("t")
                || normalizedValue.equals("yes")
                || normalizedValue.equals("y");
    }

    private static String filterInput(String input) {
        StringBuilder filteredInput = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
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

    private static String formatIc(double indexOfCoincidence) {
        return String.format("%.6f", indexOfCoincidence);
    }

    private static String colorize(String text, double value, double min, double max) {
        return COLORS[colorIndex(value, min, max)] + text + RESET_COLOR;
    }

    private static int colorIndex(double value, double min, double max) {
        if (max <= min) {
            return 0;
        }

        return (int) Math.round((value - min) * (COLORS.length - 1) / (max - min));
    }

    private static class CosetICAnalysis {
        final List<KeyLengthResult> results;
        final double minAverageIc;
        final double maxAverageIc;
        final double minCosetIc;
        final double maxCosetIc;

        CosetICAnalysis(
                List<KeyLengthResult> results,
                double minAverageIc,
                double maxAverageIc,
                double minCosetIc,
                double maxCosetIc
        ) {
            this.results = results;
            this.minAverageIc = minAverageIc;
            this.maxAverageIc = maxAverageIc;
            this.minCosetIc = minCosetIc;
            this.maxCosetIc = maxCosetIc;
        }
    }

    private static class KeyLengthResult {
        final int keyLength;
        final double[] cosetIcs;
        final int[] cosetLengths;
        final double averageIc;
        final int longestCosetLength;

        KeyLengthResult(
                int keyLength,
                double[] cosetIcs,
                int[] cosetLengths,
                double averageIc,
                int longestCosetLength
        ) {
            this.keyLength = keyLength;
            this.cosetIcs = cosetIcs;
            this.cosetLengths = cosetLengths;
            this.averageIc = averageIc;
            this.longestCosetLength = longestCosetLength;
        }
    }
}
