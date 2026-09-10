package Modules.Utils;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static ArrayList<String> tokenize(String input) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean escaping = false;
        boolean tokenStarted = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Backslash escapes the next character.
            if (escaping) {
                current.append(c);
                escaping = false;
                tokenStarted = true;
                continue;
            }

            if (c == '\\') {
                escaping = true;
                tokenStarted = true;
                continue;
            }

            // Inside single quotes.
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                } else {
                    current.append(c);
                }
                continue;
            }

            // Inside double quotes.
            if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                } else {
                    current.append(c);
                }
                continue;
            }

            // Start/end single quote.
            if (c == '\'') {
                inSingleQuote = true;
                tokenStarted = true;
                continue;
            }

            // Start/end double quote.
            if (c == '"') {
                inDoubleQuote = true;
                tokenStarted = true;
                continue;
            }

            // Whitespace ends a token.
            if (Character.isWhitespace(c)) {
                if (tokenStarted) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    tokenStarted = false;
                }
                continue;
            }

            current.append(c);
            tokenStarted = true;
        }

        // Check for malformed input.
        if (escaping) { throw new TokenizeException("Trailing escape character"); }
        if (inSingleQuote) { throw new TokenizeException("Unterminated single quote"); }
        if (inDoubleQuote) { throw new TokenizeException("Unterminated double quote"); }
        if (tokenStarted) { tokens.add(current.toString()); }

        return tokens;
    }

    public static class TokenizeException extends RuntimeException {
        public TokenizeException(String message) {
            super(message);
        }
    }
}
//```
//
//    ### Example
//
//```java
//String input =
//        "vigenere \"my text.txt\" --key \"SECRET KEY\" --decrypt";
//
//List<String> tokens = CommandTokenizer.tokenize(input);
//
//for (String token : tokens) {
//    System.out.println("[" + token + "]");
//}
//```
//
//Produces:
//
//        ```text
//[vigenere]
//        [my text.txt]
//        [--key]
//        [SECRET KEY]
//        [--decrypt]
//        ```
