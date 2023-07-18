package better.reload.plugin.listener;

import better.reload.api.ReloadEvent;
import better.reload.plugin.BetterReload;
import better.reload.plugin.util.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This class listens for when the ReloadEvent is sent and reloads BetterReload's configuration when it is called. It's
 * essentially just showing how the event can be used while providing some minor functionality.
 */
public class ReloadListener implements Listener {

    @EventHandler
    public void onReload(ReloadEvent event) {
        Configuration.reload();
        BetterReload.PLUGIN.getReloadCommand().regenerateTabCompletions();
    }
}
