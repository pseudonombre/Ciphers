package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import static Modules.Utils.InputParsing.parse;

public class AsNumbers {

    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("decrypt", 'd', null, false) }
        );

        StringBuilder ret = new StringBuilder();

        if(!(boolean) args[0].value){
            for (char c : input.toCharArray()) {
                if (Character.isLetter(c)) {
                    ret.append(Character.toUpperCase(c) - 'A');
                    ret.append(' ');
                } else {
                    ret.append(c);
                }
            }
        } else {
            for (String s : input.split(" ")) {
                if (s.matches("\\d+"))
                    ret.append((char) ('a' + Integer.parseInt(s)));
                else
                    ret.append(s).append(' ');
            }
        }

        return ret.toString();
    }
}
