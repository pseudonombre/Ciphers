package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import static Modules.Utils.InputParsing.parse;

public class Affine {
    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("a", 'a', String.class, null),
                        new InputParsing.Argument("b", 'b', String.class, null),
                        new InputParsing.Argument("decrypt", 'd', null, false) }
        );

        int a = parseLetter((String) args[0].value);
        int b = parseLetter((String) args[1].value);
//        if ((boolean) args[0].value) {
//            shiftAmount = -shiftAmount;
//        }

        StringBuilder shiftedText = new StringBuilder();
        if ((boolean) args[2].value){
            int axainv = a;
            for (int i = 0; i < 26; i++) {
                axainv += a;
                if (Math.floorMod(axainv, 26) == 1){
                    break;
                }
            }
            a = axainv / a;
            System.out.println("axainv = " + axainv);
            System.out.println("a = " + a);
            for (char c : input.toCharArray()) {
                shiftedText.append(shiftChar(c, a * (Character.toUpperCase(c) - 'A' - b)  ));
            }
        } else {
            for (char c : input.toCharArray()) {
                shiftedText.append(shiftChar(c, a * (Character.toUpperCase(c) - 'A') + b  ));
            }
        }

        return shiftedText.toString();
    }

    private static int parseLetter(String value) {
        if (value.length() == 1 && Character.isLetter(value.charAt(0))) {
            return Character.toUpperCase(value.charAt(0)) - 'A';
        }
        return Integer.parseInt(value);
    }

    public static char shiftChar(char c, int shiftAmount) {
        if (c >= 'A' && c <= 'Z') {
            return (char) ('A' + Math.floorMod(shiftAmount, 26));
        }

        if (c >= 'a' && c <= 'z') {
            return (char) ('a' + Math.floorMod(shiftAmount, 26));
        }
        return c;
    }
}
