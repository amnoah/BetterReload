package better.reload.plugin.external;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.*;

public class ExternalManager {

    private static final Map<String, PluginWrapper> EXTERNAL_RELOAD_MAP = new HashMap<>();

    public static void clear() {
        EXTERNAL_RELOAD_MAP.clear();
    }

    public static @Nullable PluginWrapper getExternalReloads(String plugin) {
        plugin = plugin.toLowerCase();
        return EXTERNAL_RELOAD_MAP.get(plugin);
    }

    public static Set<String> getKeys() {
        return EXTERNAL_RELOAD_MAP.keySet();
    }

    public static Collection<PluginWrapper> getValues() {
        return EXTERNAL_RELOAD_MAP.values();
    }

    public static void registerExternalReloads(String name, @Nullable Plugin plugin, ExternalReload... reloads) {
        name = name.toLowerCase();
        PluginWrapper wrapper = EXTERNAL_RELOAD_MAP.get(name);
        if (wrapper == null) wrapper = new PluginWrapper(plugin);
        wrapper.registerExternalReloads(reloads);
        EXTERNAL_RELOAD_MAP.put(name, wrapper);
    }
}
