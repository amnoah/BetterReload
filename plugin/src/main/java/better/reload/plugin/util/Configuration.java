package better.reload.plugin.util;

import better.reload.plugin.BetterReload;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * This class both loads configuration values and caches them. It's honestly not really needed as I don't really see
 * a point for a plugin this small to have reloading functionality, but it does provide a good example of how the reload
 * event could be used.
 */
public class Configuration {

    // Corresponds to the "version" field in config.yml
    private static final String VERSION = "1.0";

    private Configuration() {}

    public static boolean LOG_MESSAGES;

    public static String START_RELOAD_MESSAGE;
    public static String END_RELOAD_MESSAGE;
    public static String PLUGIN_NOT_SUPPORTED_MESSAGE;

    public static List<String> COMMANDS;

    /**
     * This void will load the configuration file and update cached values.
     */
    public static void reload() {
        BetterReload.PLUGIN.saveDefaultConfig();
        BetterReload.PLUGIN.reloadConfig();
        FileConfiguration config = BetterReload.PLUGIN.getConfig();

        // Make sure the config file is up-to-date.

        String currentVersion = config.getString("version");
        if (currentVersion == null || !currentVersion.equals(VERSION)) {
            try {
                // The data folder should exist, there's larger problems if it doesn't.
                String name = "old-config-" + BetterReload.PLUGIN.getDataFolder().listFiles().length + ".yml";

                BetterReload.PLUGIN.getLogger().warning("Replacing old configuration file! Moving it to " + name + ".");

                File source = new File(config.getCurrentPath());
                File dest = new File(BetterReload.PLUGIN.getDataFolder().getPath() + name);

                if (!dest.createNewFile()) {
                    BetterReload.PLUGIN.getLogger().warning("Could not generate old configuration file!");
                    return;
                }

                Files.copy(source.toPath(), dest.toPath());

                if (!source.delete()) {
                    BetterReload.PLUGIN.getLogger().warning("Could not replace configuration file!");
                    return;
                }

                BetterReload.PLUGIN.saveDefaultConfig();
                BetterReload.PLUGIN.reloadConfig();
            } catch (Exception e) {
                BetterReload.PLUGIN.getLogger().warning("Could not regenerate the configuration file!");
                BetterReload.PLUGIN.getLogger().warning("Please delete the current config.yml and restart the server.");
            }
            config = BetterReload.PLUGIN.getConfig();
        }

        // Cache config settings.

        LOG_MESSAGES = config.getBoolean("log-messages");

        START_RELOAD_MESSAGE = config.getString("start-reload-message");
        END_RELOAD_MESSAGE = config.getString("end-reload-message");
        PLUGIN_NOT_SUPPORTED_MESSAGE = config.getString("plugin-not-supported-message");

        COMMANDS = config.getStringList("commands");
    }
}
