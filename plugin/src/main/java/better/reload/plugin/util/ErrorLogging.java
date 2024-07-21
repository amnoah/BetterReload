package better.reload.plugin.util;

import better.reload.plugin.BetterReload;
import org.bukkit.plugin.RegisteredListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class adds custom error logging.
 * Due to a reload potentially reloading many plugins at once, it could absolutely flood console if multiple were to
 * have errors or exceptions. Thus, we log them, print a simple message, and guide the user to where they're logged.
 */
public class ErrorLogging {

    private static final DateTimeFormatter dateFormatter;
    private static final Path folder;

    static {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH_mm_ss");
        folder = BetterReload.PLUGIN.getDataFolder().toPath().resolve("logs");
    }

    /**
     * Logs the exception involving the listener to a file in the BetterReload folder.
     */
    public static void log(RegisteredListener listener, Throwable throwable) {

        /*
         * For once Spigot actually improved something as they've updated?!?
         *
         * Legacy versions abstract any throwable into an EventException when an event throws a throwable. Modern
         * versions let you have access to the original throwable. Having access to the original throwable lets you
         * have a little bit more information that could be helpful.
         *
         * Could potentially more directly interact with listeners via reflection to avoid this in legacy, but that
         * would unnecessarily slow reloads and cause potential issues if the Spigot API changes.
         */

        try {
            BetterReload.PLUGIN.getLogger().warning("Logging an error for " + listener.getPlugin().getName() + "...");

            String time = LocalDateTime.now().format(dateFormatter);
            File outputFile = createFile(time);
            BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

            out.write("Time: " + time);
            out.newLine();
            out.write("Plugin: " + listener.getPlugin().getName());
            out.newLine();
            out.write("Listener: " + listener.getListener().getClass().getName());
            out.newLine();
            out.write("Throwable Type: " + throwable.getClass().getName());
            out.newLine();

            if (throwable.getMessage() != null) {
                out.write("Throwable Message: " + throwable.getMessage());
                out.newLine();
            }

            out.newLine();
            out.newLine();
            out.write("Stacktrace:");
            out.newLine();

            for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                out.write(stackTraceElement.toString());
                out.newLine();
            }

            out.close();

            BetterReload.PLUGIN.getLogger().warning("Error has been logged in file " + outputFile.getPath() + "!");
        } catch (Exception e) {
            BetterReload.PLUGIN.getLogger().warning("BetterReload could not properly log a throwable!");
            e.printStackTrace();
        }
    }

    /**
     * Creates a file with the given time as the name.
     * Not used anywhere else, but separated to reduce code complexity.
     */
    private static File createFile(String time) throws IOException {
        if (!Files.exists(folder)) Files.createDirectories(folder);
        File file = folder.resolve(time + ".log").toFile();

        int count = 1;
        while (!file.createNewFile()) {
            file = folder.resolve(time + "-" + count + ".log").toFile();
            count++;
        }

        return file;
    }
}
