package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import static Modules.Utils.InputParsing.parse;

public class CeasarShift {
    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("shift", 's', String.class, null),
                        new InputParsing.Argument("decrypt", 'd', null, false) }
        );
//        if (moduleArgs.length != 2) {
//            throw new IllegalArgumentException("Usage: ceasarshift <shift letter|number> <reverse>");
//        }

        int shiftAmount = parseShiftAmount((String) args[0].value);
        if ((boolean) args[1].value) {
            shiftAmount = -shiftAmount;
        }
//        int shiftAmount = parseShiftAmount(moduleArgs[0]);
//        if (parseBooleanArg(moduleArgs[1])) {
//            shiftAmount = -shiftAmount;
//        }

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
