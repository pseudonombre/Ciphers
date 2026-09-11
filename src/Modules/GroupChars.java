package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import static Modules.Utils.InputParsing.parse;

public class GroupChars {
    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("groupSize", 'g', int.class, null),
                                new InputParsing.Argument("rowSize", 'r', int.class, null) }
        );
        //arg 0: group size
        int groupSize = (int) args[0].value;
        //arg 1: groups on a row
        int rowSize = (int) args[1].value;

        input = input.replaceAll("\\s+", "");

        StringBuilder sb = new StringBuilder();
        String s = "";
        int i = 0;
        while (true) {
            try {
                s = input.substring(0, groupSize);
                input = input.substring(groupSize);
                sb.append(s + " ");
                i ++;
                if (i == rowSize) {
                    sb.append('\n');
                    i = 0;
                }
            } catch (IndexOutOfBoundsException e) {
                // this will happen if there is an incomplete or nonextant
                // group, ending the cycle
                sb.append(input);
                break;
            }
        }
        return sb.toString();
    }

}
