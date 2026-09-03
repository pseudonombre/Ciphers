package Modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Write {

    private static final Path SAVED_TEXT_DIR = Path.of("src", "SavedText");
    public static String function(String input, String[] moduleArgs) {
        if (moduleArgs.length < 1) {
            throw new IllegalArgumentException("A filename is required.");
        }

        if (moduleArgs[0].indexOf('.') == -1) {
            moduleArgs[0] += ".txt";
        }

        Path file = Paths.get(String.valueOf(SAVED_TEXT_DIR), moduleArgs[0]);


        boolean append = moduleArgs.length >= 2
                && (moduleArgs[1].equalsIgnoreCase("add")
                || moduleArgs[1].equalsIgnoreCase("a"));

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
