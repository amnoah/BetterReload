package better.reload.plugin.util;

import better.reload.plugin.BetterReload;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This class both loads configuration values and caches them. It's honestly not really needed as I don't really see
 * a point for a plugin this small to have reloading functionality, but it does provide a good example of how the reload
 * event could be used.
 */
public class Configuration {

    // Corresponds to the "version" field in config.yml
    private static final String VERSION = "2";

    private Configuration() {}

    public static boolean LOG_MESSAGES;

    public static String START_RELOAD_MESSAGE;
    public static String END_RELOAD_MESSAGE;
    public static String ERROR_MESSAGE;
    public static String PLUGIN_NOT_SUPPORTED_MESSAGE;

    public static List<String> CONSOLE_COMMANDS;
    public static List<String> PLAYER_COMMANDS;

    /**
     * This void will load the configuration file and update cached values.
     */
    public static void reload() {
        BetterReload.getPlugin().saveDefaultConfig();
        BetterReload.getPlugin().reloadConfig();
        FileConfiguration config = BetterReload.getPlugin().getConfig();

        // Cache config settings.

        LOG_MESSAGES = config.getBoolean("log-messages");

        START_RELOAD_MESSAGE = config.getString("start-reload-message");
        END_RELOAD_MESSAGE = config.getString("end-reload-message");
        ERROR_MESSAGE = config.getString("error-message");
        PLUGIN_NOT_SUPPORTED_MESSAGE = config.getString("plugin-not-supported-message");

        CONSOLE_COMMANDS = config.getStringList("console-commands");
        PLAYER_COMMANDS = config.getStringList("player-commands");
    }
}
