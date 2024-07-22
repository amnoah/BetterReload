package better.reload.plugin.listener;

import better.reload.plugin.command.ReloadCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Arrays;

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
        String[] messageElems = event.getBuffer().split(" ");

        switch (messageElems[0].toLowerCase()) {
            case "/reload":
            case "/rl":
                if (messageElems.length == 1 || event.getBuffer().endsWith(" ")) event.setCompletions(reloadCommand.onTabComplete(null, null, null, null));
                else event.setCompletions(reloadCommand.onTabComplete(null, null, null, Arrays.copyOfRange(messageElems, 1, messageElems.length)));
        }
    }
}
