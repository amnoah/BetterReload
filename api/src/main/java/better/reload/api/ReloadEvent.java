package better.reload.api;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is the basis of the entire project. When sent out, the ReloadEvent indicates that plugins should reload
 * their configurations. It provides access to the CommandSender who triggered the reload.
 */
public class ReloadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CommandSender commandSender;

    public ReloadEvent(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    /*
     * Getters.
     */

    /**
     * Returns the command sender that initiated the reload.
     */
    public CommandSender getCommandSender() {
        return commandSender;
    }

    /**
     * Returns the static handlers list for Bukkit operations.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the static handlers list for public operations.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
