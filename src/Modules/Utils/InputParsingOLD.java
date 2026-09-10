package Modules.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InputParsingOLD {

    public static ParsedArgs parseArgs(String[] args, String... acceptedTags) {
        Set<String> acceptedTagSet = new HashSet<>();
        for (String acceptedTag : acceptedTags) {
            acceptedTagSet.add(normalizeTagName(acceptedTag));
        }

        List<String> positionalArgs = new ArrayList<>();
        Map<String, String> taggedArgs = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("--") || arg.length() == 2) {
                positionalArgs.add(arg);
                continue;
            }

            String tagExpression = arg.substring(2);
            String tagName;
            String value;
            int equalsIndex = tagExpression.indexOf('=');

            if (equalsIndex >= 0) {
                tagName = normalizeTagName(tagExpression.substring(0, equalsIndex));
                value = tagExpression.substring(equalsIndex + 1);
            } else {
                tagName = normalizeTagName(tagExpression);
                if (!acceptedTagSet.contains(tagName)) {
                    positionalArgs.add(arg);
                    continue;
                }

                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for --" + tagName);
                }

                value = args[++i];
            }

            if (!acceptedTagSet.contains(tagName)) {
                positionalArgs.add(arg);
                if (equalsIndex < 0) {
                    positionalArgs.add(value);
                }
                continue;
            }

            if (taggedArgs.containsKey(tagName)) {
                throw new IllegalArgumentException("Duplicate value for --" + tagName);
            }

            taggedArgs.put(tagName, value);
        }

        return new ParsedArgs(positionalArgs, taggedArgs);
    }

    public static int parseIntArg(String value, String argName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(argName + " must be an integer");
        }
    }

    public static int parsePositiveIntArg(String value, String argName) {
        int parsedValue = parseIntArg(value, argName);
        if (parsedValue < 1) {
            throw new IllegalArgumentException(argName + " must be at least 1");
        }
        return parsedValue;
    }

    public static boolean isBooleanArg(String value) {
        String normalizedValue = value.toLowerCase();
        return normalizedValue.equals("true")
                || normalizedValue.equals("t")
                || normalizedValue.equals("yes")
                || normalizedValue.equals("y")
                || normalizedValue.equals("1")
                || normalizedValue.equals("false")
                || normalizedValue.equals("f")
                || normalizedValue.equals("no")
                || normalizedValue.equals("n")
                || normalizedValue.equals("0");
    }

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

    private static String normalizeTagName(String tagName) {
        return tagName.toLowerCase();
    }

    public static class ParsedArgs {
        private final List<String> positionalArgs;
        private final Map<String, String> taggedArgs;

        private ParsedArgs(List<String> positionalArgs, Map<String, String> taggedArgs) {
            this.positionalArgs = positionalArgs;
            this.taggedArgs = taggedArgs;
        }

        public int positionalCount() {
            return positionalArgs.size();
        }

        public String positional(int index) {
            return positionalArgs.get(index);
        }

        public String tagged(String tagName) {
            return taggedArgs.get(normalizeTagName(tagName));
        }

        public boolean hasTagged(String tagName) {
            return taggedArgs.containsKey(normalizeTagName(tagName));
        }

        public String value(String argName, int positionalIndex) {
            return value(argName, positionalIndex, null);
        }

        public String value(String argName, int positionalIndex, String defaultValue) {
            String positionalValue = positionalIndex >= 0 && positionalIndex < positionalArgs.size()
                    ? positionalArgs.get(positionalIndex)
                    : null;
            String taggedValue = tagged(argName);

            if (positionalValue != null && taggedValue != null && !positionalValue.equals(taggedValue)) {
                throw new IllegalArgumentException("Conflicting values for " + argName);
            }

            if (positionalValue != null) {
                return positionalValue;
            }

            if (taggedValue != null) {
                return taggedValue;
            }

            return defaultValue;
        }

        public String value(String[] argNames, int positionalIndex, String defaultValue) {
            String positionalValue = positionalIndex >= 0 && positionalIndex < positionalArgs.size()
                    ? positionalArgs.get(positionalIndex)
                    : null;
            String taggedValue = null;
            String taggedArgName = null;

            for (String argName : argNames) {
                String candidate = tagged(argName);
                if (candidate == null) {
                    continue;
                }

                if (taggedValue != null && !taggedValue.equals(candidate)) {
                    throw new IllegalArgumentException(
                            "Conflicting values for " + Arrays.toString(argNames)
                    );
                }

                taggedValue = candidate;
                taggedArgName = argName;
            }

            if (positionalValue != null && taggedValue != null && !positionalValue.equals(taggedValue)) {
                throw new IllegalArgumentException("Conflicting values for " + taggedArgName);
            }

            if (positionalValue != null) {
                return positionalValue;
            }

            if (taggedValue != null) {
                return taggedValue;
            }

            return defaultValue;
        }

        public String requiredValue(String argName, int positionalIndex) {
            String value = value(argName, positionalIndex);
            if (value == null) {
                throw new IllegalArgumentException("Missing required argument: " + argName);
            }
            return value;
        }

        public String requiredValue(String[] argNames, int positionalIndex) {
            String value = value(argNames, positionalIndex, null);
            if (value == null) {
                throw new IllegalArgumentException("Missing required argument: " + Arrays.toString(argNames));
            }
            return value;
        }

        public int positiveInt(String argName, int positionalIndex) {
            return parsePositiveIntArg(requiredValue(argName, positionalIndex), argName);
        }

        public int positiveInt(String[] argNames, int positionalIndex) {
            return parsePositiveIntArg(requiredValue(argNames, positionalIndex), argNames[0]);
        }

        public boolean bool(String argName, int positionalIndex, boolean defaultValue) {
            String value = value(argName, positionalIndex);
            if (value == null) {
                return defaultValue;
            }
            return parseBooleanArg(value);
        }

        public boolean bool(String[] argNames, int positionalIndex, boolean defaultValue) {
            String value = value(argNames, positionalIndex, null);
            if (value == null) {
                return defaultValue;
            }
            return parseBooleanArg(value);
        }
    }
}