package better.reload.plugin.external;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * This represents an individual external reload for a plugin.
 * An external reload is represented as a node under a plugin's node in the config.
 */
public class ExternalReload {

    private Executor executor = Executor.CONSOLE;
    private Stage stage = Stage.POST;
    private final List<String> commands;

    /**
     * Initialize the ExternalReload object.
     */
    public ExternalReload(ConfigurationSection section) {
        String executorName = section.getString("executor", "CONSOLE");
        String stageName = section.getString("stage", "POST");

        // There may be plans to add more executors and stages in the future, so I don't want to hardcode.

        for (Executor e : Executor.values()) {
            if (!e.toString().equalsIgnoreCase(executorName)) continue;
            executor = e;
            break;
        }

        for (Stage s : Stage.values()) {
            if (!s.toString().equalsIgnoreCase(stageName)) continue;
            stage = s;
            break;
        }

        commands = section.getStringList("commands");
    }

    public enum Executor {
        CONSOLE, // The command should be executed as console regardless of the sender.
        SENDER   // The command should be executed as the sender.
    }

    public enum Stage {
        PRE,    // The command should be sent prior to the reload event being sent.
        POST    // The command should be sent after the reload event is sent.
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

    /**
     * Return the stage that this external reload should be performed at.
     */
    public Stage getStage() {
        return stage;
    }
}
