package better.reload.plugin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allows for the easy translation and removal of color codes from Strings.
 */
public class ChatColor {

    // Taken from Bukkit's ChatColor.
    private static final Pattern PATTERN = Pattern.compile("(?i)&[0-9A-FK-ORX]");

    private ChatColor() {}

    /**
     * Return a version of the message with no color codes.
     * The only difference from Bukkit's method is that it looks for the '&' symbol instead of the '§' symbol.
     */
    public static String stripColorCodes(String input) {
        return PATTERN.matcher(input).replaceAll("");
    }

    /**
     * Convert a message with '&' color codes to one with '§' color codes.
     */
    public static String translateColorCodes(String input) {
        Matcher matcher = PATTERN.matcher(input);
        StringBuffer output = new StringBuffer();

        while (matcher.find()) {
            String match = matcher.group();
            String replacement = "§" + match.substring(1);
            matcher.appendReplacement(output, replacement);
        }

        matcher.appendTail(output);
        return output.toString();
    }
}
