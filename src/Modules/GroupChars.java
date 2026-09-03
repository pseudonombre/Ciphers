package Modules;

public class GroupChars {
    public static String function(String input, String[] moduleArgs) {
        //arg 0: group size
        int groupSize = Integer.parseInt(moduleArgs[0]);
        //arg 1: groups on a row
        int rowSize = Integer.parseInt(moduleArgs[1]);

        input = input.replaceAll("\s+", "");

        StringBuilder sb = new StringBuilder();
        String s = "";
        int i = 0;
        while (true) {
            try {
                s = input.substring(0, groupSize);
                input = input.substring(groupSize);
                sb.append(s + " ");
                i ++;
                if (i == rowSize) {
                    sb.append('\n');
                    i = 0;
                }
            } catch (IndexOutOfBoundsException e) {
                // this will happen if there is an incomplete or nonextant
                // group, ending the cycle
                sb.append(input);
                break;
            }
        }
        return sb.toString();
    }

}
