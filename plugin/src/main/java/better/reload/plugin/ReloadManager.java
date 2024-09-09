package better.reload.plugin;

import better.reload.api.ReloadEvent;
import better.reload.plugin.external.ExternalManager;
import better.reload.plugin.external.ExternalReload;
import better.reload.plugin.external.PluginWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class provides the basic functionality behind the reloading process.
 * It has been separated from the reload command itself so other plugins can also call these methods as deemed necessary.
 */
public class ReloadManager {

    /**
     * This enum describes the outcome of a reload.
     */
    public enum Status {
        FAILURE,    // A plugin produced a throwable during the reload.
        SUCCESS,    // No plugins produced a throwable during the reload.
        UNSUPPORTED // The plugin had no registered ReloadEvent listener.
    }

    /**
     * Initiate a reload for all plugins using the console as the CommandSender.
     * If a single plugin fails, a FAILURE status will be returned. If all succeed, a SUCCESS will be returned.
     */
    public static Status reload() {
        return reload(Bukkit.getConsoleSender());
    }

    /**
     * Initiate a reload for all plugins using a given CommandSender.
     * If a single plugin fails, a FAILURE status will be returned.
     * If all succeed, a SUCCESS will be returned.
     */
    public static Status reload(CommandSender commandSender) {
        // Collect all supported plugins.
        Collection<Plugin> supportedPlugins = new HashSet<>();
        for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
            supportedPlugins.add(listener.getPlugin());
        }

        Collection<PluginWrapper> wrappers = ExternalManager.getValues();

        boolean success = true;

        // Cycle through all plugins and attempt to find a corresponding plugin wrapper.
        for (Plugin plugin : supportedPlugins) {
            PluginWrapper wrapper = ExternalManager.getExternalReloads(plugin.getName());
            wrappers.remove(wrapper);
            if (reload(commandSender, plugin, wrapper) == Status.FAILURE) success = false;
        }

        // Cycle through all plugin wrappers not associated with any supported plugins.
        for (PluginWrapper wrapper : wrappers) {
            if (reload(commandSender, null, wrapper) == Status.FAILURE) success = false;
        }

        if (success) return Status.SUCCESS;
        return Status.FAILURE;
    }

    /**
     * Initiate a reload for the given plugin using the console as the CommandSender.
     * If no reload has occurred whatsoever, it will return UNSUPPORTED.
     * If any throwable is thrown during te process, it will return FAILURE.
     * Otherwise, it will return SUCCESS.
     */
    public static Status reload(String plugin) {
        return reload(Bukkit.getConsoleSender(), plugin);
    }

    /**
     * Initiate a reload for the given plugin using a given CommandSender.
     * If no reload has occurred whatsoever, it will return UNSUPPORTED.
     * If any throwable is thrown during te process, it will return FAILURE.
     * Otherwise, it will return SUCCESS.
     */
    public static Status reload(CommandSender commandSender, String plugin) {
        return reload(commandSender, Bukkit.getPluginManager().getPlugin(plugin), ExternalManager.getExternalReloads(plugin));
    }

    /**
     * Initiate a reload for the given plugin using the console as the CommandSender.
     * If no reload has occurred whatsoever, it will return UNSUPPORTED.
     * If any throwable is thrown during te process, it will return FAILURE.
     * Otherwise, it will return SUCCESS.
     */
    public static Status reload(Plugin plugin) {
        return reload(Bukkit.getConsoleSender(), plugin);
    }

    /**
     * Initiate a reload for the given plugin using a given CommandSender.
     * If no reload has occurred whatsoever, it will return UNSUPPORTED.
     * If any throwable is thrown during te process, it will return FAILURE.
     * Otherwise, it will return SUCCESS.
     */
    public static Status reload(CommandSender commandSender, Plugin plugin) {
        return reload(commandSender, plugin, ExternalManager.getExternalReloads(plugin.getName()));
    }

    /**
     * This function handles the actual reloading process. All other reload functions resolve to this function.
     * If no reload has occurred whatsoever, it will return UNSUPPORTED.
     * If any throwable is thrown during te process, it will return FAILURE.
     * Otherwise, it will return SUCCESS.
     */
    public static Status reload(CommandSender commandSender, Plugin plugin, PluginWrapper wrapper) {
        if (plugin == null && wrapper == null) return Status.UNSUPPORTED;
        boolean reloaded = false;
        Status partial;
        ReloadEvent event = new ReloadEvent(commandSender);

        // Execute pre commands.
        partial = executeCommands(commandSender, wrapper, ExternalReload.Stage.PRE);
        switch (partial) {
            case FAILURE:
                return partial;
            case SUCCESS:
                reloaded = true;
        }

        // Attempt to reload the plugin via event.
        if (plugin != null) {
            for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
                try {
                    if (listener.getPlugin() == plugin) {
                        reloaded = true;
                        listener.callEvent(event);
                    }
                } catch (Throwable throwable) {
                    ErrorLogger.log(listener, throwable);
                    return Status.FAILURE;
                }
            }
        }

        // Execute post commands.
        partial = executeCommands(commandSender, wrapper, ExternalReload.Stage.POST);
        switch (partial) {
            case FAILURE:
                return partial;
            case SUCCESS:
                reloaded = true;
        }

        if (reloaded) return Status.SUCCESS;
        return Status.UNSUPPORTED;
    }

    /**
     * Run all commands for the given plugin wrapper at the given stage.
     */
    public static Status executeCommands(CommandSender commandSender, PluginWrapper wrapper, ExternalReload.Stage stage) {
        if (wrapper == null) return Status.UNSUPPORTED;

        // Cycle through all registered reloads with this wrapper and run the ones for the given stage.
        for (ExternalReload reload : wrapper.getExternalReloads()) {
            if (reload.getStage() != stage) continue;
            for (String command : reload.getCommands()) {
                try {
                    switch (reload.getExecutor()) {
                        case CONSOLE:
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            continue;
                        case SENDER:
                            Bukkit.dispatchCommand(commandSender, command);
                    }
                // Can dispatching a command throw a throwable? I'm not sure, this may get removed.
                } catch (Throwable throwable) {
                    ErrorLogger.log(wrapper, throwable);
                    return Status.FAILURE;
                }
            }
        }

        return Status.SUCCESS;
    }
}