package better.reload.plugin;

import better.reload.plugin.configuration.Configuration;
import better.reload.plugin.listener.PreCommandProcessing;
import better.reload.plugin.listener.Reload;
import better.reload.plugin.listener.ReloadCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main class for the plugin. This class handles the loading and disabling of all the listeners and bStats.
 */
public class BetterReload extends JavaPlugin {

    public static final int B_STATS_ID = 19094;

    public static BetterReload PLUGIN;
    public static Metrics METRICS;

    @Override
    public void onEnable() {
        PLUGIN = this;

        METRICS = new Metrics(this, B_STATS_ID);

        Configuration.reload();

        getCommand("reload").setExecutor(new ReloadCommand());
        getServer().getPluginManager().registerEvents(new PreCommandProcessing(), this);
        getServer().getPluginManager().registerEvents(new Reload(), this);
    }

    @Override
    public void onDisable() {
        PLUGIN = null;

        if (METRICS != null) METRICS.shutdown();
        METRICS = null;

        HandlerList.unregisterAll(this);
    }
}
