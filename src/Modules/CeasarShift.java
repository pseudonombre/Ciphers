package Modules;

public class CeasarShift {
    public static String function(String input, String[] moduleArgs) {
        if (moduleArgs.length != 2) {
            throw new IllegalArgumentException("Usage: ceasarshift <shift letter|number> <reverse>");
        }

        int shiftAmount = parseShiftAmount(moduleArgs[0]);
        if (parseBooleanArg(moduleArgs[1])) {
            shiftAmount = -shiftAmount;
        }

        StringBuilder shiftedText = new StringBuilder();

        for (char c : input.toCharArray()) {
            shiftedText.append(shiftChar(c, shiftAmount));
        }

        return shiftedText.toString();
    }

    private static int parseShiftAmount(String value) {
        if (value.length() == 1 && Character.isLetter(value.charAt(0))) {
            return Character.toUpperCase(value.charAt(0)) - 'A';
        }

        return Integer.parseInt(value);
    }

    private static boolean parseBooleanArg(String value) {
        String normalizedValue = value.toLowerCase();
        return normalizedValue.equals("true")
                || normalizedValue.equals("t")
                || normalizedValue.equals("yes")
                || normalizedValue.equals("y");
    }

    public static char shiftChar(char c, int shiftAmount) {
        if (c >= 'A' && c <= 'Z') {
            return shiftWithinAlphabet(c, 'A', shiftAmount);
        }

        if (c >= 'a' && c <= 'z') {
            return shiftWithinAlphabet(c, 'a', shiftAmount);
        }

        return c;
    }

    private static char shiftWithinAlphabet(char c, char alphabetStart, int shiftAmount) {
        int alphabetSize = 26;
        int offset = c - alphabetStart;
        int shiftedOffset = Math.floorMod(offset + shiftAmount, alphabetSize);

        return (char) (alphabetStart + shiftedOffset);
    }
}
