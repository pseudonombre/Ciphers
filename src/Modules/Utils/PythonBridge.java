package Modules.Utils;

import java.io.*;

public class PythonBridge {

    private static Process process;
    private static BufferedWriter input;
    private static BufferedReader output;

    public static void start() throws IOException {

        process = new ProcessBuilder(
                "python",
                "src/server.py"
        )
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        input = new BufferedWriter(
                new OutputStreamWriter(
                        process.getOutputStream()));

        output = new BufferedReader(
                new InputStreamReader(
                        process.getInputStream()));
    }

    public static String call(
            String function,
            String argument) throws IOException {

        input.write(function);
        input.write("\t");
        input.write(argument);
        input.newLine();
        input.flush();

        return output.readLine();
    }

    public static void stop() throws IOException {

        input.write("EXIT");
        input.newLine();
        input.flush();

        process.destroy();
    }
}