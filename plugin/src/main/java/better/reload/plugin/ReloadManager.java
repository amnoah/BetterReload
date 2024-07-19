package better.reload.plugin;

import better.reload.api.ReloadEvent;
import better.reload.plugin.util.Configuration;
import better.reload.plugin.util.ErrorLogging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

/**
 * This class provides the basic functionality behind the reloading process.
 * It has been separated from the reload command itself so other plugins can also call these methods as deemed necessary.
 */
public class ReloadManager {

    /**
     * Initiate a reload for all plugins using the console as the CommandSender.
     */
    public static void reload() {
        reload(Bukkit.getConsoleSender());
    }

    /**
     * Initiate a reload for all plugins using a given CommandSender.
     */
    public static void reload(CommandSender commandSender) {
        ReloadEvent event = new ReloadEvent(commandSender);

        /*
         * We could use Bukkit.getPluginManager().callEvent(), but I'd prefer to use my own error logging system.
         * So, we manually pass this event to all applicable listeners.
         */
        for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
            try {
                listener.callEvent(event);
            } catch (EventException exception) {
                ErrorLogging.log(listener, exception);
            }
        }
    }

    /**
     * Initiate a reload for the given plugin using the console as the CommandSender.
     * Return whether the plugin has a listener for the event.
     */
    public static boolean reload(Plugin plugin) {
        return reload(Bukkit.getConsoleSender(), plugin);
    }

    /**
     * Initiate a reload for the given plugin using a given CommandSender.
     * Return whether the plugin has a listener for the event.
     */
    public static boolean reload(CommandSender commandSender, Plugin plugin) {
        if (plugin == null) return false;
        boolean reloaded = false;
        ReloadEvent event = new ReloadEvent(commandSender);

        for (RegisteredListener listener : ReloadEvent.getHandlerList().getRegisteredListeners()) {
            try {
                if (listener.getPlugin() == plugin) {
                    reloaded = true;
                    listener.callEvent(event);
                }
            } catch (EventException exception) {
                ErrorLogging.log(listener, exception);
            }
        }

        return reloaded;
    }

    /**
     * This represents the stage at which commands should be performed.
     * This can either be before the event was sent (pre) or after the event was sent (post).
     */
    public enum CommandStage {
        PRE_RELOAD("pre:"),
        POST_RELOAD("post:");

        private final String configName;

        CommandStage(String configName) {
            this.configName = configName;
        }

        public String getConfigName() {
            return configName;
        }
    }

    /**
     * Run applicable commands from the BetterReload config according to the CommandStage and CommandSender.
     */
    public static void runCommands(CommandStage stage, CommandSender commandSender) {
        if (commandSender instanceof Player) {
            for (String command : Configuration.PLAYER_COMMANDS) {
                String[] commandElements = command.split(":", 3);
                if (commandElements.length <= 2) continue;
                if (!commandElements[0].toLowerCase().startsWith(stage.getConfigName())) continue;

                switch (commandElements[1].toLowerCase()) {
                    case "player":
                        Bukkit.dispatchCommand(commandSender, commandElements[2].replace("%ReloadCommandSender%", commandSender.getName()));
                        break;
                    case "console":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandElements[2]);
                        break;
                    default:
                        BetterReload.PLUGIN.getLogger().warning("Unsupported player command mode!");
                        BetterReload.PLUGIN.getLogger().warning("Player command mode: " + commandElements[1]);
                }
            }
        } else if (commandSender instanceof ConsoleCommandSender) {
            for (String command : Configuration.CONSOLE_COMMANDS) {
                if (!command.toLowerCase().startsWith(stage.getConfigName())) continue;
                if (command.length() == stage.getConfigName().length()) continue;
                Bukkit.dispatchCommand(commandSender, command.substring(stage.getConfigName().length()));
            }
        } else {
            BetterReload.PLUGIN.getLogger().warning("Unsupported CommandSender in runCommands method!");
            BetterReload.PLUGIN.getLogger().warning("CommandSender type: " + commandSender.getClass().getName());
        }
    }
}
