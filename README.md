# BetterReload

Let's be honest, Bukkit's reload command is based on a flawed idea which often ends up causing more problems than
benefits. It's to the point where forks like Spigot and Paper have added messages warning users against the use of the
command, telling them to rather restart the server. So, what if the reload command was replaced entirely by a better
system?

Inspired by the [SpongePowered](https://github.com/SpongePowered/Sponge) project, BetterReload swaps Bukkit's attempt
at literally reloading all plugins to an event that is passed along to plugins instead. Upon issuing the /reload
command with BetterReload installed, plugins that depend on BetterReload can receive an event that leaves the reloading
process up to the plugins.

As of update 1.4.0, the plugin also includes a configurable system allowing any plugin to be supported via the external
section of the config.yml file. Please read more about this [here](https://github.com/amnoah/BetterReload/wiki/Config#external-reload).
Ideally plugins should support the event, but this ensures universal support.

## Features
- Overloads the `/reload` command and accessible through `/BetterReload`.
- Sends a reload event to all plugins if no argument is passed into the command.
- Sends a reload event to specific plugins if arguments are passed into the command.
- Custom error logging that will keep console nice and log as much information as possible.
- Smart tab completion (only on `/BetterReload` in legacy versions, on both commands in modern).
- Bukkit's reload command is still accessible through `/bukkit:reload`.
- Ability to add other plugin reload commands to the reload process.

## Limitations
- `BlockCommandSender`s (typically command blocks) cannot use the `/reload` and must rather use `/BetterReload`.

# Documentation

For information on how to use BetterReload's commands, please check the [Command wiki](https://github.com/amnoah/BetterReload/wiki/Command).

For information on how to configure BetterReload, please check the [Config wiki](https://github.com/amnoah/BetterReload/wiki/Config).

For information on how to support the ReloadEvent, please check the [ReloadEvent Support wiki](https://github.com/amnoah/BetterReload/wiki/ReloadEvent-Support).

For information on how to support the ReloadManager, please check the [ReloadManager Support wiki](https://github.com/amnoah/BetterReload/wiki/ReloadManager-Support).

For a list of known supported plugins, please check the [Supported Plugins wiki](https://github.com/amnoah/BetterReload/wiki/Supported-Plugins).
# Support

For general support, please join my [Discord server](https://discord.gg/ey9uTg3hcy).

For issues with the project, please open an issue in the issues tab.
