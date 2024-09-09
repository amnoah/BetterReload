package better.reload.plugin.command;

import better.reload.api.ReloadEvent;
import better.reload.plugin.BetterReload;
import better.reload.plugin.ReloadManager;
import better.reload.plugin.external.ExternalManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;

/**
 * This class runs the logic behind our custom reload command. It's rather basic, just sending a few messages and
 * calling an event through Bukkit's event system.
 */
public class ReloadCommand implements CommandExecutor, TabExecutor {

    private boolean logMessages;
    private String startReload, endReload, error, unsupported;

    private List<String> autoComplete;

    /*
     * Command completion methods.
     */

    /**
     * This void will be run when the "/betterreload:reload" command is run.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final long start = System.currentTimeMillis();

        if (!startReload.isEmpty()) sendMessage(commandSender, startReload);

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
            for (String pluginName : strings) {
                switch (ReloadManager.reload(commandSender, pluginName)) {
                    case FAILURE:
                        sendMessage(commandSender, error);
                        break;
                    case UNSUPPORTED:
                        sendMessage(commandSender, unsupported.replaceAll("%input%", pluginName));
                }
            }
        // If there's no additional input then call an event for all plugins.
        } else {
            if (ReloadManager.reload(commandSender).equals(ReloadManager.Status.FAILURE)) {
                sendMessage(commandSender, error);
            }
        }

        String end = endReload.replaceAll("%ms%",String.valueOf(System.currentTimeMillis() - start));
        if (!end.isEmpty()) sendMessage(commandSender, end);

        return true;
    }

    /**
     * This will return a list of tab-completions specifically for the "/betterreload:reload" command.
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (autoComplete == null) regenerateTabCompletions();

        // If there's no text entered, autocomplete all commands.
        if (strings == null || strings.length == 0 || strings[strings.length - 1].isEmpty()) return autoComplete;

        // Otherwise find out what commands start with this input.
        List<String> output = new ArrayList<>();
        for (String out : autoComplete) if (out.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase())) output.add(out);
        return output;
    }

    /*
     * Config related methods.
     */

    /**
     * Update the cache of plugin names to display in a tab completion.
     */
    public void regenerateTabCompletions() {
        // Initially use a set so only 1 of each can be put in.
        Set<String> names = new HashSet<>();

        // Register all plugins with a registered listener.
        for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
            names.add(listener.getPlugin().getName().toLowerCase());
        }

        // Register all external reload commands.
        names.addAll(ExternalManager.getKeys());

        // Generate the tab completion list.
        autoComplete = new ArrayList<>();
        autoComplete.addAll(names);
        Collections.sort(autoComplete);
        autoComplete = Collections.unmodifiableList(autoComplete);
    }

    /**
     * Reload all config settings for the command.
     */
    public void reload(ConfigurationSection config) {
        logMessages = config.getBoolean("log-messages", false);

        startReload = config.getString("start-reload-message", "&b&lBR > &r&7Starting reload...");
        startReload = ChatColor.translateAlternateColorCodes('&', startReload);
        endReload = config.getString("end-reload-message", "&b&lBR > &r&7Reload finished in &b%ms% &7ms!");
        endReload = ChatColor.translateAlternateColorCodes('&', endReload);
        error = config.getString("error-message", "&b&lBR > &r&cAn error occurred while reloading! Please check console for more info!");
        error = ChatColor.translateAlternateColorCodes('&', error);
        unsupported = config.getString("plugin-not-supported-message", "&b&lBR > &r&cCould not find a reload listener for &b%input%&c!");
        unsupported = ChatColor.translateAlternateColorCodes('&', unsupported);
    }

    /*
     * Utility methods.
     */

    /**
     * This void reduces redundancy in the process of sending messages.
     */
    private void sendMessage(CommandSender commandSender, String string) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(string);
            if (logMessages) BetterReload.getPlugin().getLogger().info(ChatColor.stripColor(string));
        } else BetterReload.getPlugin().getLogger().info(ChatColor.stripColor(string));
    }
}