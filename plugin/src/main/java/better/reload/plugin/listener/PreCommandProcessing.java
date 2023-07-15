package better.reload.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * This class modifies commands sent by players and the server if they run the command "/reload".
 * From what I can tell Bukkit takes priority for handling the command "/reload", meaning we can't simply override it by
 * registering our own "/reload". Thus, when a player runs "/reload" we replace their command with "/BetterReload:Reload"
 * before the initial command is processed. Bukkit's reload method can still be accessed through "/Bukkit:Reload".
 */
public class PreCommandProcessing implements Listener {

    /**
     * This void will modify commands from players before they're processed.
     */
    @EventHandler
    public void handleCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split(" ")[0].equalsIgnoreCase("/reload")) event.setMessage("/BetterReload:Reload");
    }

    /**
     * This void will modify commands from the server before they're processed.
     */
    @EventHandler
    public void handleServerCommand(ServerCommandEvent event) {
        if (event.getCommand().split(" ")[0].equalsIgnoreCase("reload")) event.setCommand("BetterReload:Reload");
    }
}
