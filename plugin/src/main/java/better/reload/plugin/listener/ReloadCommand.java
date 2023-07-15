package better.reload.plugin.listener;

import better.reload.api.ReloadEvent;
import better.reload.plugin.BetterReload;
import better.reload.plugin.configuration.Configuration;
import better.reload.plugin.util.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class runs the logic behind our custom reload command. It's rather basic, just sending a few messages and
 * calling an event through Bukkit's event system.
 */
public class ReloadCommand implements CommandExecutor {

    /**
     * This void will be run when the "/BetterReload:Reload" command is run.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final long startReload = System.currentTimeMillis();

        String start = Configuration.START_RELOAD_MESSAGE, end;
        if (!start.isEmpty()) sendMessage(commandSender, start);

        Bukkit.getPluginManager().callEvent(new ReloadEvent(commandSender));

        end = Configuration.END_RELOAD_MESSAGE.replaceAll("%ms%",String.valueOf(System.currentTimeMillis() - startReload));
        if (!end.isEmpty()) sendMessage(commandSender, end);

        return false;
    }

    /**
     * This void reduces redundancy in the process of sending messages. It's only used twice, but it cleans up the code.
     */
    public void sendMessage(CommandSender commandSender, String string) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(ChatColor.translateColorCodes(string));
            if (Configuration.LOG_MESSAGES) BetterReload.PLUGIN.getLogger().info(ChatColor.stripColorCodes(string));
        } else {
            string = ChatColor.stripColorCodes(string);
            commandSender.sendMessage(string);
            if (Configuration.LOG_MESSAGES) BetterReload.PLUGIN.getLogger().info(string);
        }
    }
}
