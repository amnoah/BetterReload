package better.reload.plugin.listener;

import better.reload.plugin.command.ReloadCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

/**
 * This class listens for tab completions and modifies it if it's for the command "/reload". We cannot directly modify
 * the tab completion in the "/reload" command itself, so we just modify what is sent to the player before it is sent.
 */
public class TabCompleteListener implements Listener {

    private final ReloadCommand reloadCommand;

    public TabCompleteListener(ReloadCommand reloadCommand) {
        this.reloadCommand = reloadCommand;
    }

    @EventHandler
    public void handleTabComplete(TabCompleteEvent event) {
        switch (event.getBuffer().split(" ")[0].toLowerCase()) {
            case "/reload":
            case "/rl":
                event.setCompletions(reloadCommand.getTabComplete());
                break;
            default:
                break;
        }
    }
}
