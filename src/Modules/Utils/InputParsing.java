package Modules.Utils;

public class InputParsing {

    public static boolean parseBooleanArg(String value) {
        String normalizedValue = value.toLowerCase();
        if (normalizedValue.equals("true")
                || normalizedValue.equals("t")
                || normalizedValue.equals("yes")
                || normalizedValue.equals("y")
                || normalizedValue.equals("1")){
            return true;
        }

        if (normalizedValue.equals("false")
                || normalizedValue.equals("f")
                || normalizedValue.equals("no")
                || normalizedValue.equals("n")
                || normalizedValue.equals("0")){
            return false;
        }

        throw new IllegalArgumentException("Unparseable Boolean");
    }
}
