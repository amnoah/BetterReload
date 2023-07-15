package better.reload.plugin.configuration;

import better.reload.plugin.BetterReload;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * This class both loads configuration values and caches them. It's honestly not really needed as I don't really see
 * a point for a plugin this small to have reloading functionality, but it does provide a good example of how the reload
 * event could be used.
 */
public class Configuration {

    private Configuration() {}

    public static boolean LOG_MESSAGES;

    public static String START_RELOAD_MESSAGE;
    public static String END_RELOAD_MESSAGE;

    /**
     * This void will load the configuration file and update cached values.
     */
    public static void reload() {
        BetterReload.PLUGIN.saveDefaultConfig();
        BetterReload.PLUGIN.reloadConfig();
        FileConfiguration config = BetterReload.PLUGIN.getConfig();

        LOG_MESSAGES = config.getBoolean("log-messages");
        START_RELOAD_MESSAGE = config.getString("start-reload-message");
        END_RELOAD_MESSAGE = config.getString("end-reload-message");
    }
}
