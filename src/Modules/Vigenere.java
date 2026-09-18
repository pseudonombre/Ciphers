package Modules;
import Modules.Utils.InputParsing;
import Modules.Utils.InputParsingUtils;
import Modules.Utils.Tokenizer;

public class Vigenere {
    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = InputParsing.parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("key", 'k', String.class, null),
                                new InputParsing.Argument("decrypt", 'd', null, false) }
        );

        String key = (String) args[0].value;
        key = key.toUpperCase();
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key must contain at least one letter");
        }

        boolean forward = !(boolean) args[1].value;
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
