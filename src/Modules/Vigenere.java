package Modules;
import Modules.Utils.InputParsing;

public class Vigenere {
    public static String function(String input, String[] moduleArgs) {
        if (moduleArgs.length != 2) {
            throw new IllegalArgumentException("Usage: vigenere <key> <forward>");
        }

        String key = filterKey(moduleArgs[0]);
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key must contain at least one letter");
        }

        boolean forward = InputParsing.parseBooleanArg(moduleArgs[1]);
        StringBuilder transformedText = new StringBuilder();
        int keyIndex = 0;

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                int shiftAmount = key.charAt(keyIndex % key.length()) - 'A';
                if (!forward) {
                    shiftAmount = -shiftAmount;
                }

                transformedText.append(CeasarShift.shiftChar(c, shiftAmount));
                keyIndex++;
            } else {
                transformedText.append(c);
            }
        }

        return transformedText.toString();
    }

    private static String filterKey(String key) {
        StringBuilder filteredKey = new StringBuilder();

        for (char c : key.toCharArray()) {
            if (Character.isLetter(c)) {
                filteredKey.append(Character.toUpperCase(c));
            }
        }

        return filteredKey.toString();
    }
}
