package better.reload.plugin.command;

import better.reload.api.ReloadEvent;
import better.reload.plugin.BetterReload;
import better.reload.plugin.util.ChatColor;
import better.reload.plugin.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class runs the logic behind our custom reload command. It's rather basic, just sending a few messages and
 * calling an event through Bukkit's event system.
 */
public class ReloadCommand implements CommandExecutor, TabExecutor {

    private List<String> autoComplete;

    /*
     * Getters.
     */

    /**
     * This function will return a list of plugin names to use in tab-completion.
     * If the list does not already exist it will generate it when first run.
     */
    public List<String> getTabComplete() {
        if (autoComplete == null) {
            autoComplete = new ArrayList<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) autoComplete.add(plugin.getName());
            autoComplete = Collections.unmodifiableList(autoComplete);
        }

        return autoComplete;
    }

    /*
     * Functions.
     */

    /**
     * This void will be run when the "/betterreload:reload" command is run.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final long startReload = System.currentTimeMillis();

        String start = Configuration.START_RELOAD_MESSAGE;
        if (!start.isEmpty()) sendMessage(commandSender, start);

        ReloadEvent event = new ReloadEvent(commandSender);

        // If there's additional inputs attempt a reload for the specific inputted plugins.
        if (strings.length > 0) {
            /*
             * Intended features:
             *
             * Preserving the order run. For example, "/reload plugin1 plugin2 plugin3" will reload the plugins in the
             * order "plugin1 plugin2 plugin3".
             *
             * Calling all registered listeners for a plugin.
             *
             * Allowing plugins to reload multiple times. For example, "/reload plugin1 plugin2 plugin1" will reload
             * plugin1 twice.
             */

            List<Plugin> plugins = new ArrayList<>();

            // Verify whether the inputted plugin names correspond with real plugins.
            for (String pluginName : strings) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if (plugin != null) {
                    plugins.add(plugin);
                } else {
                    sendMessage(commandSender, Configuration.PLUGIN_NOT_FOUND_MESSAGE.replaceAll("%input%", pluginName));
                }
            }

            // Attempt a reload for all plugins.
            for (Plugin plugin : plugins) {
                boolean reloaded = false;

                for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
                    if (listener.getPlugin() != plugin) continue;

                    try {
                        listener.callEvent(event);
                        reloaded = true;
                    } catch (EventException ignored) {
                        BetterReload.PLUGIN.getLogger().warning("Error reloading " + plugin.getName() + "!");
                    }
                }

                if (!reloaded) sendMessage(commandSender, Configuration.PLUGIN_NOT_SUPPORTED_MESSAGE.replaceAll("%input%", plugin.getName()));
            }
        // If there's no additional input then call an event for all plugins.
        } else Bukkit.getPluginManager().callEvent(event);

        String end = Configuration.END_RELOAD_MESSAGE.replaceAll("%ms%",String.valueOf(System.currentTimeMillis() - startReload));
        if (!end.isEmpty()) sendMessage(commandSender, end);

        return false;
    }

    /**
     * This will return a list of tab-completions specifically for the "/betterreload:reload" command.
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return getTabComplete();
    }

    /**
     * This void reduces redundancy in the process of sending messages.
     */
    public void sendMessage(CommandSender commandSender, String string) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(ChatColor.translateColorCodes(string));
            if (Configuration.LOG_MESSAGES) BetterReload.PLUGIN.getLogger().info(ChatColor.stripColorCodes(string));
        } else BetterReload.PLUGIN.getLogger().info(ChatColor.stripColorCodes(string));
    }
}
