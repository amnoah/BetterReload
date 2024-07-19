package better.reload.example.listener;

import better.reload.api.ReloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This class listens for when the ReloadEvent is sent and "reloads" Example's configuration..
 */
public class Reload implements Listener {

    @EventHandler
    public void onReload(ReloadEvent event) {
        event.getCommandSender().sendMessage("Hey, you ran the reload command!");
        event.getCommandSender().sendMessage("Beep, boop, I'm reloaded now!");

        // Let's test the new error logging system!
        throw new Error("funsies");
    }
}
