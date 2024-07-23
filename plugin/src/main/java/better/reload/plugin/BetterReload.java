package better.reload.plugin;

import better.reload.api.ReloadEvent;
import better.reload.plugin.command.ReloadCommand;
import better.reload.plugin.listener.PreCommandProcessingListener;
import better.reload.plugin.listener.ReloadListener;
import better.reload.plugin.listener.TabCompleteListener;
import better.reload.plugin.util.Configuration;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

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

        Configuration.reload();

        getCommand("reload").setExecutor(reloadCommand);
        getCommand("reload").setTabCompleter(reloadCommand);

        // This class is not present on older versions (1.12-? not sure) but allows tab completion on modern versions.
        try {
            Class.forName("org.bukkit.event.server.TabCompleteEvent");
            getServer().getPluginManager().registerEvents(new TabCompleteListener(reloadCommand), this);
        } catch (ClassNotFoundException ignored) {
            getLogger().warning("Tab Completion is not supported in your version.");
        }

        getServer().getPluginManager().registerEvents(new PreCommandProcessingListener(), this);
        getServer().getPluginManager().registerEvents(new ReloadListener(), this);

        // Start our metrics.

        metrics = new Metrics(this, B_STATS_ID);

        metrics.addCustomChart(new SimplePie("registered_listeners", () ->
                String.valueOf(ReloadEvent.getHandlerList().getRegisteredListeners().length)
        ));

        metrics.addCustomChart(new SimplePie("supported_plugins", () -> {
            List<Plugin> plugins = new ArrayList<>();
            for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
                if (plugins.contains(listener.getPlugin())) continue;
                plugins.add(listener.getPlugin());
            }

            return String.valueOf(plugins.size());
        }));

        metrics.addCustomChart(new SimplePie("console_commands", () ->
                String.valueOf(!Configuration.CONSOLE_COMMANDS.isEmpty())
        ));

        metrics.addCustomChart(new SimplePie("player_commands", () ->
                String.valueOf(!Configuration.PLAYER_COMMANDS.isEmpty())
        ));
    }

    @Override
    public void onDisable() {
        PLUGIN = null;

        if (metrics != null) metrics.shutdown();
        metrics = null;

        HandlerList.unregisterAll(this);
    }
}
