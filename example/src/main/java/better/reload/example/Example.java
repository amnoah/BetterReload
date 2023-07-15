package better.reload.example;

import better.reload.example.listener.Reload;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main class for the plugin. This class handles the loading and disabling of the reload listener.
 */
public class Example extends JavaPlugin {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("BetterReload") != null)
            Bukkit.getPluginManager().registerEvents(new Reload(), this);
        else getLogger().warning("BetterReload isn't present so I can't start my Reload listener :(");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
