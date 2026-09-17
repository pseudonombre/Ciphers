import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

import Modules.*;

public class Main {
    private static final Path SAVED_TEXT_DIR = Path.of("src", "SavedText");
    // Module takes: input string, arguments

    private static final Map<String, BiFunction<String, String, String>> MODULES =
            new HashMap<>();
    static String cws = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Module registration
        loadModules();
        try {
            Modules.Utils.PythonBridge.start();
        } catch (Exception e){
            System.out.println("An error occurred in loading the python server.\n" +
                    "Some modules may be unavailable.\n" +
                    "\tError: " + e);
        }

        System.out.println("Welcome to CipherLab! (That's such a generic working name)");

        cws = "";

        while (true) {
            System.out.print("> ");
            String instruction = scanner.nextLine().trim();

            if (instruction.equalsIgnoreCase("exit")) {
                break;
            }
            if (instruction.equalsIgnoreCase("mods")) {
                for (String module : MODULES.keySet()) {
                    System.out.println(module);
                }
                continue;
            }
            if (instruction.equalsIgnoreCase("ls")) {
                try (var files = Files.list(SAVED_TEXT_DIR)) {
                    files.forEach((file) -> {
                        System.out.println(file.subpath(file.getNameCount() - 1, file.getNameCount()));
                    });
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

    private static void loadModules() {
        MODULES.put("echo", (input, argString) -> input);
        MODULES.put("group", GroupChars::function);
        MODULES.put("remws", (input, argString) -> input.replaceAll("\\s+", ""));
        MODULES.put("removewhitespace", (input, argString) -> input.replaceAll("\\s+", ""));
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
        MODULES.put("sw", SplitWords::function);
        MODULES.put("read", Read::function);
        MODULES.put("write", Write::function);
    }

    private static void executeInstruction(String instruction) throws IOException {
        if (instruction.indexOf(' ') == -1) {
            cws = runModule(instruction, cws, "");
        } else {
            cws = runModule(instruction.substring(0, instruction.indexOf(" ")), cws, instruction.substring(instruction.indexOf(" ") + 1));
        }
        System.out.println(cws);
    }

    private static String runModule(String moduleName, String input, String moduleArgs) {
        BiFunction<String, String, String> module = MODULES.get(moduleName.toLowerCase());

        if (module == null) {
            throw new IllegalArgumentException(
                    "Unknown module: " + moduleName
            );
        }

        return module.apply(input, moduleArgs);
    }
}