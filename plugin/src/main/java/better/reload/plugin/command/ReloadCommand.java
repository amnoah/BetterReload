package better.reload.plugin.command;

import better.reload.api.ReloadEvent;
import better.reload.plugin.BetterReload;
import better.reload.plugin.ReloadManager;
import better.reload.plugin.external.ExternalManager;
import better.reload.plugin.util.ChatColor;
import better.reload.plugin.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;

/**
 * This class runs the logic behind our custom reload command. It's rather basic, just sending a few messages and
 * calling an event through Bukkit's event system.
 */
public class ReloadCommand implements CommandExecutor, TabExecutor {

    private List<String> autoComplete;

    /**
     * This void will be run when the "/betterreload:reload" command is run.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final long startReload = System.currentTimeMillis();

        String start = Configuration.START_RELOAD_MESSAGE;
        if (!start.isEmpty()) sendMessage(commandSender, start);

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
                        sendMessage(commandSender, Configuration.ERROR_MESSAGE);
                        break;
                    case UNSUPPORTED:
                        sendMessage(commandSender, Configuration.PLUGIN_NOT_SUPPORTED_MESSAGE.replaceAll("%input%", pluginName));
                }
            }
        // If there's no additional input then call an event for all plugins.
        } else {
            if (ReloadManager.reload(commandSender).equals(ReloadManager.Status.FAILURE)) {
                sendMessage(commandSender, Configuration.ERROR_MESSAGE);
            }
        }

        String end = Configuration.END_RELOAD_MESSAGE.replaceAll("%ms%",String.valueOf(System.currentTimeMillis() - startReload));
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

    /**
     * Update the cache of plugin names to display in a tab completion.
     */
    public void regenerateTabCompletions() {
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
     * This void reduces redundancy in the process of sending messages.
     */
    private void sendMessage(CommandSender commandSender, String string) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(ChatColor.translateColorCodes(string));
            if (Configuration.LOG_MESSAGES) BetterReload.getPlugin().getLogger().info(ChatColor.stripColorCodes(string));
        } else BetterReload.getPlugin().getLogger().info(ChatColor.stripColorCodes(string));
    }
}