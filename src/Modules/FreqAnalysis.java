package Modules;
import java.util.HashMap;
import java.util.Map;

public class FreqAnalysis {

    public static String function(String input, String[] moduleArgs) {
        String res = input;
//        String[] strings = input.split("\s+");
        Map<String, Integer> frequency = new HashMap<>();



        // Everything that is not a letter or digit is a delimiter.
        String[] words = input.split("[^\\p{L}\\p{N}]+");

        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("[^\\p{L}\\p{N}]+").matcher(input);

        java.util.List<String> delimiters = new java.util.ArrayList<>();

        while (matcher.find()) {
            delimiters.add(matcher.group());
        }
        delimiters.toArray(new String[0]);



        // Count frequencies
        for (String s : words) {
            frequency.merge(s, 1, Integer::sum);
        }

        int maxFrequency = frequency.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(1);

        int position = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String s = words[i];
            String d = delimiters.get(i);
            int count = frequency.get(s);

            int colorIndex = (int) ((count - 1) * (colors.length - 1)
                    / (double) (maxFrequency - 1));

            // Handle the case where every string occurs only once
            if (maxFrequency == 1) {
                colorIndex = 0;
            }

            sb.append(colors[colorIndex] + s + "\u001B[0m" + d);
            //System.out.print(colors[colorIndex] + s + "\u001B[0m ");
        }

        return sb.toString();
    }
//    public static String function(String input, String[] moduleArgs) {
//        String res = input;
//        String[] strings = input.split("\s+");
//        Map<String, Integer> frequency = new HashMap<>();
//        // Count frequencies
//        for (String s : strings) {
//            frequency.merge(s, 1, Integer::sum);
//        }
//
//        int maxFrequency = frequency.values().stream()
//                .mapToInt(Integer::intValue)
//                .max()
//                .orElse(1);
//
//        int position = 0;
//        for (String s : frequency.keySet()) {
//            int count = frequency.get(s);
//
//            int colorIndex = (int) ((count - 1) * (colors.length - 1)
//                    / (double) (maxFrequency - 1));
//
//            // Handle the case where every string occurs only once
//            if (maxFrequency == 1) {
//                colorIndex = 0;
//            }
//
//            res = res.replaceAll(s, colors[colorIndex] + s + "\u001B[0m");
//            //System.out.print(colors[colorIndex] + s + "\u001B[0m ");
//        }
//
//        return res;
//    }
    static String[] colors = {
            "\u001B[34m", // blue
            "\u001B[36m", // cyan
            "\u001B[96m", // bright cyan
            "\u001B[32m", // green
            "\u001B[92m", // bright green
            "\u001B[33m", // yellow
            "\u001B[93m", // bright yellow
            "\u001B[35m", // magenta
            "\u001B[95m", // bright magenta
            "\u001B[31m"  // red
    };
}
