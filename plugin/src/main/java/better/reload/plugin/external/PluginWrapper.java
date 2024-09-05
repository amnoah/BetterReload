package better.reload.plugin.external;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginWrapper {

    private final Plugin plugin;
    private final List<ExternalReload> externalReloads = new ArrayList<>();

    public PluginWrapper(@Nullable Plugin plugin) {
        this.plugin = plugin;
    }

    public List<ExternalReload> getExternalReloads() {
        return externalReloads;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void registerExternalReloads(ExternalReload... reload) {
        externalReloads.addAll(Arrays.asList(reload));
    }
}
