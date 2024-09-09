package better.reload.plugin.external;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class
 */
public class ExternalManager {

    private static final Map<String, PluginWrapper> EXTERNAL_RELOAD_MAP = new HashMap<>();

    /**
     * Prevent initialization, this is a static-only class.
     */
    private ExternalManager() {}

    /*
     * Getters.
     */

    /**
     * Return the plugin wrapper associated with the given plugin name.
     * Will be null if there is no such value.
     */
    public static @Nullable PluginWrapper getExternalReloads(String plugin) {
        plugin = plugin.toLowerCase();
        return EXTERNAL_RELOAD_MAP.get(plugin);
    }

    /**
     * Return all plugin names in the map.
     */
    public static Set<String> getKeys() {
        return EXTERNAL_RELOAD_MAP.keySet();
    }

    /**
     * Return all plugin wrappers in the map.
     */
    public static Collection<PluginWrapper> getValues() {
        return EXTERNAL_RELOAD_MAP.values();
    }

    /*
     * Map Modifications.
     */

    /**
     * Remove all entries from the external reload map.
     */
    public static void clear() {
        EXTERNAL_RELOAD_MAP.clear();
    }

    /**
     * Register external reloads with the given plugin name and plugin object.
     */
    public static void registerExternalReloads(String name, @Nullable Plugin plugin, List<ExternalReload> reloads) {
        name = name.toLowerCase();
        PluginWrapper wrapper = EXTERNAL_RELOAD_MAP.get(name);
        if (wrapper == null) wrapper = new PluginWrapper(plugin);
        wrapper.registerExternalReloads(reloads);
        EXTERNAL_RELOAD_MAP.put(name, wrapper);
    }

    /*
     * Methods.
     */

    /**
     * Grab all external reload data from the configuration file and fill the map.
     */
    public static void reload(@Nullable ConfigurationSection section) {
        clear();
        if (section == null) return;

        for (String plugin : section.getKeys(false)) {
            ConfigurationSection pluginSection = section.getConfigurationSection(plugin);
            Plugin p = Bukkit.getPluginManager().getPlugin(plugin);
            List<ExternalReload> externalReloads = new ArrayList<>();

            for (String command : pluginSection.getKeys(false)) {
                ConfigurationSection commandSection = pluginSection.getConfigurationSection(command);
                externalReloads.add(new ExternalReload(commandSection));
            }

            registerExternalReloads(plugin, p, externalReloads);
        }
    }
}
