package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static Modules.Utils.InputParsing.parse;

public class Read {

    private static final Path SAVED_TEXT_DIR = Path.of("src", "SavedText");

    public static String function(String input, String argString) {//throws IOException {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("input", 'i', String.class, null),
                                new InputParsing.Argument("isText", 't', null, false) }
        );
        input = (String) args[0].value; //moduleArgs[0];
        String lowerInput = input.toLowerCase();
        boolean isText = (boolean) args[1].value;

        if (isText) {//(lowerInput.startsWith("text:") | lowerInput.startsWith("t:")) {
            return input.substring(input.indexOf(':') + 1);
        }

        else {// (lowerInput.startsWith("file:") | lowerInput.startsWith("f:")) {
            String filename = input.substring(input.indexOf(':') + 1);
            if (filename.indexOf('.') == -1) {
                filename += ".txt";
            }
            Path file = SAVED_TEXT_DIR.resolve(filename);

            if (!Files.exists(file)) {
                System.out.println("File not found: " + file);
                return "";
            }
            try {
                return Files.readString(file);
            } catch (IOException e) {
                System.out.println("Something unexpected happened when accessing " + file);
                return "";
            }
        }
    }
}
