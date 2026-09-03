package Modules;

import java.util.HashMap;
import java.util.Map;

public class IC {
    private static final double IC_SCALE_MIN = 0.0;
    private static final double IC_SCALE_MAX = 0.1;
    private static final double IC_ENGLISH = 0.06864;
    private static final double IC_RANDOM = 0.03847;
    private static final int IC_BAR_WIDTH = 50;

    public static String function(String input, String[] moduleArgs) {
        double indexOfCoincidence = calculate(input);

        System.out.println("IC: " + indexOfCoincidence);
        System.out.println("IC(English) = " + IC_ENGLISH);
        System.out.println("IC(Random) = " + IC_RANDOM);
        System.out.println(buildIcBar(indexOfCoincidence));
        return input;
    }

    public static double calculate(String input) {
        Map<Character, Integer> counts = new HashMap<>();
        int total = 0;

        for (char c: input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                counts.merge(c, 1, Integer::sum);
                total++;
            }
        }

        double indexOfCoincidence = 0;
        if (total > 1) {
            long matchingPairs = 0;
            for (int count : counts.values()) {
                matchingPairs += (long) count * (count - 1);
            }
            indexOfCoincidence = matchingPairs / (double) (total * (total - 1));
        }

        return indexOfCoincidence;
    }

    private static String buildIcBar(double indexOfCoincidence) {
        char[] bar = new char[IC_BAR_WIDTH + 1];
        for (int i = 0; i < bar.length; i++) {
            bar[i] = '-';
        }

        bar[positionFor(IC_RANDOM)] = 'R';
        bar[positionFor(IC_ENGLISH)] = 'E';
        bar[positionFor(indexOfCoincidence)] = 'I';

        return String.format(
                "0.000 [%s] 0.100%nI = IC, E = IC(English), R = IC(Random)",
                new String(bar)
        );
    }

    private static int positionFor(double value) {
        double clamped = Math.max(IC_SCALE_MIN, Math.min(IC_SCALE_MAX, value));
        double ratio = (clamped - IC_SCALE_MIN) / (IC_SCALE_MAX - IC_SCALE_MIN);
        return (int) Math.round(ratio * IC_BAR_WIDTH);
    }
}
