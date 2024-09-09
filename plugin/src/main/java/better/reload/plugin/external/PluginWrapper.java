package better.reload.plugin.external;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a plugin's external reloads.
 * No new plugins should ever be initialized while the server is running, so we don't have to associate any external
 * reloads with plugins at runtime.
 */
public class PluginWrapper {

    private final Plugin plugin;
    private final List<ExternalReload> externalReloads = new ArrayList<>();

    /**
     * Initialize the PluginWrapper with a plugin as the parameter.
     * The plugin can be null, the external reloads just will be handled on their own if so.
     */
    public PluginWrapper(@Nullable Plugin plugin) {
        this.plugin = plugin;
    }

    /*
     * Getters.
     */

    /**
     * Return the external reloads associated with this plugin.
     */
    public List<ExternalReload> getExternalReloads() {
        return externalReloads;
    }

    /**
     * Return the plugin associated with these external reloads.
     */
    public @Nullable Plugin getPlugin() {
        return plugin;
    }

    /*
     * Methods.
     */

    /**
     * Register new external reloads to this plugin.
     */
    public void registerExternalReloads(List<ExternalReload> reload) {
        externalReloads.addAll(reload);
    }
}
