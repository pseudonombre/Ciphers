import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

import Modules.*;

public class Main{
    private static final Path SAVED_TEXT_DIR = Path.of("src", "SavedText");
    // Module takes: input string, arguments
    private static final Map<String, BiFunction<String, String[], String>> MODULES =
            new HashMap<>();
    static String cws = "";

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        // Module registration
        loadModules();

        System.out.println("Welcome to CipherLab! (That's such a generic working name)");

        cws = "";

        while (true) {
            System.out.print("> ");
            String instruction = scanner.nextLine().trim();

            if (instruction.equalsIgnoreCase("exit")) {
                break;
            }
            if (instruction.equalsIgnoreCase("mods")) {
                for (String module : MODULES.keySet()){
                    System.out.println(module);
                }
                continue;
            }
            if (instruction.equalsIgnoreCase("ls")) {
                try (var files = Files.list(SAVED_TEXT_DIR)) {
                    files.forEach((file) -> {System.out.println(file.subpath(file.getNameCount() - 1, file.getNameCount()));});
                } catch (IOException e) {
                    System.out.println(e);
                }

                continue;
            }
            if (instruction.isEmpty()) {
                continue;
            }

            try {
                executeInstruction(instruction);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void loadModules(){
        MODULES.put("echo", (input, moduleArgs) -> input);
        MODULES.put("group", GroupChars::function);
        MODULES.put("remws", (input, moduleArgs) -> input.replaceAll("\s+", ""));
        MODULES.put("removewhitespace", (input, moduleArgs) -> input.replaceAll("\s+", ""));
        MODULES.put("freqa", FreqAnalysis::function);
        MODULES.put("ic", IC::function);
        MODULES.put("cosetic", CosetIC::function);
        MODULES.put("ceasarshift", CeasarShift::function);
        MODULES.put("cs", CeasarShift::function);
        MODULES.put("vigcoset", VigenereCosetShift::function);
        MODULES.put("vigcosetshift", VigenereCosetShift::function);
        MODULES.put("vigenerecosetshift", VigenereCosetShift::function);
        MODULES.put("vigenere", Vigenere::function);
        MODULES.put("veginere", Vigenere::function);
        MODULES.put("vig", Vigenere::function);
        MODULES.put("read", Read::function);
        MODULES.put("write", Write::function);
    }
    private static void executeInstruction(String instruction) throws IOException {
        String[] stages = instruction.split("\\|", -1);

        if (stages.length == 0) {
            throw new IllegalArgumentException(
                    "Usage: <module> [arguments]"
            );
        }

        for (int i = 0; i < stages.length; i++) {
            String stage = stages[i].trim();

            if (stage.isEmpty()) {
                throw new IllegalArgumentException(
                        "Empty command in pipeline"
                );
            }
            //String[] parts = stage.split("\\s+");
            String[] parts = splitKeepQuotes(stage);
            String[] moduleArgs = new String[parts.length - 1];
            System.arraycopy(parts, 1, moduleArgs, 0, moduleArgs.length);

            cws = runModule(parts[0], cws, moduleArgs);
        }

        System.out.println(cws);
    }

    private static String runModule(String moduleName, String input, String[] moduleArgs) {
        BiFunction<String, String[], String> module = MODULES.get(moduleName.toLowerCase());

        if (module == null) {
            throw new IllegalArgumentException(
                    "Unknown module: " + moduleName
            );
        }

        return module.apply(input, moduleArgs);
    }

    private static String[] splitKeepQuotes(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString());
        }

        return result.toArray(new String[0]);
    }
}
