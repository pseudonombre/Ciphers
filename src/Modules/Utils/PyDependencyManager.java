package Modules.Utils;

public class PyDependencyManager {
    public static boolean isInstalled(String programName) {
        try {
            Process process = new ProcessBuilder("python", "-c", "import", programName).start();

            return process.waitFor() == 0;

        } catch (Exception e) {
            return false;
        }
    }

    private static void install(String programName) throws Exception {
        Process process = new ProcessBuilder("python", "-m", "pip", "install", programName).inheritIO().start();

        if (process.waitFor() != 0) {
            throw new RuntimeException(
                    "Could not install " + programName
            );
        }
    }
}
