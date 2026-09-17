package Modules;

import java.io.IOException;

public class SplitWords {
    public static String function(String input, String argString) {
        try {
            return Modules.Utils.PythonBridge.call(
                    "segment",
                    input
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
