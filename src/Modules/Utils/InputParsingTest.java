package Modules.Utils;


import Modules.Utils.InputParsing.*;

import java.util.ArrayList;
import java.util.List;

import static Modules.Utils.InputParsing.parse;

public class InputParsingTest {
    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        Argument[] args = parse(argTokens,
                new Argument[]{ new Argument("aa", 'a', String.class, null),
                        new Argument("bb", 'b', int.class, null),
                        new Argument("cc", 'c', boolean.class, null) });
        System.out.println("a:" + args[0].value);
        System.out.println("b:" + args[1].value);
        System.out.println("c:" + args[2].value);
        return input;
    }
}