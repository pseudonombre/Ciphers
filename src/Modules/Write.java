package Modules;

import Modules.Utils.InputParsing;
import Modules.Utils.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Write {

    private static final Path SAVED_TEXT_DIR = Path.of("src", "SavedText");
    public static String function(String input, String argString) {
        String[] argTokens = Tokenizer.tokenize(argString).toArray(new String[0]);
        InputParsing.Argument[] args = InputParsing.parse(argTokens,
                new InputParsing.Argument[]
                        { new InputParsing.Argument("file", 'f', String.class, null),
                                new InputParsing.Argument("add", 'a', null, false) }
        );

        if (args[0].value == null){
            throw new IllegalArgumentException("A filename is required.");
        }
        String filename = (String) args[0].value;

        if (filename.indexOf('.') == -1) {
            filename += ".txt";
        }

        Path file = Paths.get(String.valueOf(SAVED_TEXT_DIR), filename);


        boolean append = (boolean) args[1].value;

        try {
            if (append) {
                Files.writeString(
                        file,
                        input,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } else {
                Files.writeString(
                        file,
                        input,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save text to " + file, e);
        }

        return input;
    }
}
