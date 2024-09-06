package better.reload.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * This class modifies command sent by players and the server if they run the command "/reload".
 * From what I can tell Bukkit takes priority for handling the command "/reload", meaning we can't simply override it by
 * registering our own "/reload". Thus, when a player runs "/reload" we replace their command with "/BetterReload:Reload"
 * before the initial command is processed. Bukkit's reload method can still be accessed through "/Bukkit:Reload".
 */
public class PreCommandProcessingListener implements Listener {

    /**
     * This void will modify command from players before they're processed.
     */
    @EventHandler
    public void handleCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String modified = alterCommand(event.getMessage(), true);
        if (modified != null) event.setMessage(modified);
    }

    /**
     * This void will modify command from the server before they're processed.
     */
    @EventHandler
    public void handleServerCommand(ServerCommandEvent event) {
        String modified = alterCommand(event.getCommand(), false);
        if (modified != null) event.setCommand(modified);
    }

    /**
     * This function removes redundancy in the process of modifying the "/reload" command.
     */
    private String alterCommand(String message, boolean slash) {
        String[] elements = message.split(" ");
        if (slash) {
            switch (message.split(" ")[0].toLowerCase()) {
                case "/reload":
                case "/rl":
                    elements[0] = "/betterreload:reload";
                    break;
                default:
                    return null;
            }
        } else {
            switch (message.split(" ")[0].toLowerCase()) {
                case "reload":
                case "rl":
                    elements[0] = "betterreload:reload";
                    break;
                default:
                    return null;
            }
        }

        return String.join(" ", elements);
    }
}
