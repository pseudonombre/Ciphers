package Modules.Utils;

import java.util.List;

public class InputParsing {
    //input: "vig APPLE f"
    //input: "vig --key APPLE --encode false"
    //input: "vig -k "APPLE" -e "0""
    public static Argument[] parse(String[] tokens, Argument[] definition) {
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            // argumentDefinition is matched with the argument the user is trying to specify,
            // first by tags and then positionally. It is later given the correct value and returned.
            Argument argumentDefinition = null;

            // Check for named argument
            if (token.startsWith("--")) {
                String name = token.substring(2);
                for (Argument argument : definition) {
                    if (argument.name.equals(name)) {
                        argumentDefinition = argument;
                        break;
                    }
                }
            }
            // Check for single-letter argument
            else if (token.startsWith("-") && token.length() == 2) {
                char code = token.charAt(1);
                for (Argument argument : definition) {
                    if (argument.singleLetterCode == code) {
                        argumentDefinition = argument;
                        break;
                    }
                }
            }

            // No argument marker: treat as positional
            else {
                for (Argument argument : definition) {
                    if (argument.value == null) {
                        argument.value = parseValue(token, argument.domain);
                        break;
                    }
                }
                continue;
            }

            // Named argument was found
            if (argumentDefinition != null) {
                // Argument requires a value
                if (argumentDefinition.domain != null) {
                    if (i + 1 >= tokens.length) {
                        throw new IllegalArgumentException("Missing value for argument: " + argumentDefinition.name);
                    }

                    argumentDefinition.value = parseValue(tokens[++i], argumentDefinition.domain);
                }
                // Argument is a flag
                else {
                    argumentDefinition.domain = boolean.class;
                    argumentDefinition.value = true;
                }
            }
        }

        return definition;
    }

    private static Object parseValue(String token, Class<?> domain) {
        if (domain == String.class) {
            return token;
        }
        if (domain == Boolean.class || domain == boolean.class) {
            return InputParsingUtils.parseBoolean(token);
        }
        if (domain == Integer.class || domain == int.class) {
            return Integer.parseInt(token);
        }
        if (domain == Double.class) {
            return Double.parseDouble(token);
        }

        throw new IllegalArgumentException("Unsupported argument type: " + domain);
    }

    public static class Argument {
        String name;
        char singleLetterCode;
        Class<?> domain;
        // a null value for domain indicates a boolean flag taking no argument
        Object value;

        public Argument(String c_name, char c_singleLetterCode, Class<?> c_domain, Object c_value) {
            this.name = c_name;
            this.singleLetterCode = c_singleLetterCode;
            this.domain = c_domain;
            this.value = c_value;
        }

        public Argument(String c_name, char c_singleLetterCode, Class<?> c_domain) {
            this(c_name, c_singleLetterCode, c_domain, null);
        }
    }
}

