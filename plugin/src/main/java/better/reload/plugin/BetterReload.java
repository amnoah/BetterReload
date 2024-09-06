package better.reload.plugin;

import better.reload.api.ReloadEvent;
import better.reload.plugin.command.ReloadCommand;
import better.reload.plugin.external.ExternalManager;
import better.reload.plugin.listener.PreCommandProcessingListener;
import better.reload.plugin.listener.ReloadListener;
import better.reload.plugin.listener.TabCompleteListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main class for the plugin. This class handles the loading and disabling of all the listeners and bStats.
 */
public final class BetterReload extends JavaPlugin {

    private static final int B_STATS_ID = 19094;
    private static BetterReload PLUGIN;

    private Metrics metrics;
    private final ReloadCommand reloadCommand = new ReloadCommand();

    /*
     * Getters.
     */

    public static BetterReload getPlugin() {
        return PLUGIN;
    }

    public ReloadCommand getReloadCommand() {
        return reloadCommand;
    }

    /*
     * Functions.
     */

    @Override
    public void onEnable() {
        PLUGIN = this;

        reload();

        getCommand("reload").setExecutor(reloadCommand);
        getCommand("reload").setTabCompleter(reloadCommand);

        // This class is not present on older versions (1.12-? not sure) but allows tab completion on modern versions.
        try {
            Class.forName("org.bukkit.event.server.TabCompleteEvent");
            getServer().getPluginManager().registerEvents(new TabCompleteListener(reloadCommand), this);
        } catch (ClassNotFoundException ignored) {
            getLogger().info("Tab Completion on the /reload command is not supported in your version.");
        }

        getServer().getPluginManager().registerEvents(new PreCommandProcessingListener(), this);
        getServer().getPluginManager().registerEvents(new ReloadListener(this), this);

        // Start our metrics.

        metrics = new Metrics(this, B_STATS_ID);

        // There is 1 listener built into BetterReload itself.
        metrics.addCustomChart(new SimplePie("using_supported_plugins", () ->
                String.valueOf(ReloadEvent.getHandlerList().getRegisteredListeners().length > 1)
        ));

        /*
        metrics.addCustomChart(new SimplePie("console_commands", () ->
                String.valueOf(!Configuration.CONSOLE_COMMANDS.isEmpty())
        ));

        metrics.addCustomChart(new SimplePie("player_commands", () ->
                String.valueOf(!Configuration.PLAYER_COMMANDS.isEmpty())
        ));.
         */
    }

    @Override
    public void onDisable() {
        PLUGIN = null;

        if (metrics != null) metrics.shutdown();
        metrics = null;

        HandlerList.unregisterAll(this);
    }

    public void reload() {
        BetterReload.getPlugin().saveDefaultConfig();
        BetterReload.getPlugin().reloadConfig();
        ConfigurationSection config = BetterReload.getPlugin().getConfig();

        reloadCommand.reload(config);
        ExternalManager.reload(config.getConfigurationSection("external"));
    }
}
