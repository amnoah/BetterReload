package better.reload.plugin.external;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * This represents an individual external reload for a plugin.
 * An external reload is represented as a node under a plugin's node in the config.
 */
public class ExternalReload {

    private Executor executor = Executor.CONSOLE;
    private final List<String> commands;

    /**
     * Initialize the ExternalReload object.
     */
    public ExternalReload(ConfigurationSection section) {
        if (section.getString("executor", "CONSOLE").equalsIgnoreCase("SENDER")) {
            executor = Executor.SENDER;
        } else {
            executor = Executor.CONSOLE;
        }

        commands = section.getStringList("commands");
    }

    public enum Executor {
        CONSOLE, // The command should be executed as console regardless of the sender.
        SENDER   // The command should be executed as the sender.
    }

    /*
     * Getters.
     */

    /**
     * Return the commands associated with this external reload.
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * Return what sort of executor should run this external reload.
     */
    public Executor getExecutor() {
        return executor;
    }
}
