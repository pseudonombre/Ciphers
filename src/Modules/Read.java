package Modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Read {

    private static final Path SAVED_TEXT_DIR = Path.of("src", "SavedText");

    public static String function(String input, String[] moduleArgs) {//throws IOException {
        input = moduleArgs[0];
        String lowerInput = input.toLowerCase();

        if (lowerInput.startsWith("text:") | lowerInput.startsWith("t:")) {
            return input.substring(input.indexOf(':') + 1);
        }

        if (lowerInput.startsWith("file:") | lowerInput.startsWith("f:")) {
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

        throw new IllegalArgumentException(
                "Input must begin with 'text:', 't:', 'file:', or 'f:'"
        );

    }
}
