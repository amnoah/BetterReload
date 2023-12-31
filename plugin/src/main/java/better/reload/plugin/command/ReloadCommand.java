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

    /**
     * This void will be run when the "/betterreload:reload" command is run.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final long startReload = System.currentTimeMillis();
        String senderName = commandSender instanceof Player ? commandSender.getName() : "";

        String start = Configuration.START_RELOAD_MESSAGE;
        if (!start.isEmpty()) sendMessage(commandSender, start);

        // Pre-Event Commands.
        runCommands("pre:", commandSender);

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
            for (String pluginName : strings) {
                boolean reloaded = false;

                for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
                    if (!pluginName.equalsIgnoreCase(listener.getPlugin().getName())) continue;

                    try {
                        listener.callEvent(event);
                        reloaded = true;
                    } catch (EventException ignored) {
                        BetterReload.PLUGIN.getLogger().warning("Error reloading " + pluginName.toLowerCase() + "!");
                    }
                }

                if (!reloaded) sendMessage(commandSender, Configuration.PLUGIN_NOT_SUPPORTED_MESSAGE.replaceAll("%input%", pluginName));
            }
        // If there's no additional input then call an event for all plugins.
        } else Bukkit.getPluginManager().callEvent(event);

        // Post-Event Commands.
        runCommands("post:", commandSender);

        String end = Configuration.END_RELOAD_MESSAGE.replaceAll("%ms%",String.valueOf(System.currentTimeMillis() - startReload));
        if (!end.isEmpty()) sendMessage(commandSender, end);

        return true;
    }

    /**
     * Runs PRE- / POST-commands as provided in the configuration file.
     */
    private static void runCommands(String stage, CommandSender commandSender) {
        if (commandSender instanceof Player) {
            for (String command : Configuration.PLAYER_COMMANDS) {
                if (!command.toLowerCase().startsWith(stage)) continue;
                if (command.length() == stage.length()) continue;

                String[] commandElements = command.substring(stage.length()).split(":", 2);
                if (commandElements.length <= 1) continue;

                switch (commandElements[0].toLowerCase()) {
                    case "player":
                        Bukkit.dispatchCommand(commandSender, commandElements[1].replaceAll("%ReloadCommandSender%", commandSender.getName()));
                        break;
                    case "console":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandElements[1]);
                        break;
                }
            }
        } else {
            for (String command : Configuration.CONSOLE_COMMANDS) {
                if (!command.toLowerCase().startsWith(stage)) continue;
                if (command.length() == stage.length()) continue;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(stage.length()));
            }
        }
    }

    /**
     * This will return a list of tab-completions specifically for the "/betterreload:reload" command.
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (autoComplete == null) regenerateTabCompletions();
        return autoComplete;
    }

    /**
     * Update the cache of plugin names to display in a tab completion.
     */
    public void regenerateTabCompletions() {
        autoComplete = new ArrayList<>();
        for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
            if (!autoComplete.contains(listener.getPlugin().getName())) autoComplete.add(listener.getPlugin().getName());
        }
        Collections.sort(autoComplete);
        autoComplete = Collections.unmodifiableList(autoComplete);
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