# BetterReload

Let's be honest, Bukkit's reload command is based on a flawed idea which often ends up causing more problems than
benefits. It's to the point where forks like Spigot and Paper have added messages warning users against the use of the
command, telling them to rather restart the server. So, what if the reload command was replaced entirely by a better
system?

Inspired by the [SpongePowered](https://github.com/SpongePowered/Sponge) project, BetterReload swaps Bukkit's attempt
at literally reloading all plugins to an event that is passed along to plugins instead. Upon issuing the /reload
command with BetterReload installed, plugins that depend on BetterReload can receive an event that leaves the reloading
process up to the plugins.

## How Does It Work?

As far as I can tell you cannot simply override a Bukkit command, leading to the decision to instead alter the command 
before it is fully processed. When the command `/reload` is sent through the server, it is swapped to `/betterreload:reload`
before being processed.

This method also allows Bukkit's `/reload` command to be accessible through `/bukkit:reload`, although I would personally
advise against its usage regardless.

## Command Usage

- Use `/reload` to send a reload event to plugins.
- Any arguments put past the command will be recognized as plugins to specifically send the event to.
- The permission node is `better.reload`.
- Example: `/reload` will send a reload event to all plugins with support built in.
- Example: `/reload plugin1` will send the reload event to only a plugin with the name plugin1.
- Example: `/reload plugin1 plugin2` will send the reload event to only a plugin with the name plugin1 and a plugin with the name plugin2.

## Limitations

- `BlockCommandSender`s (typically command blocks) cannot use the `/reload` command as there is no accessible preprocessing
  stage for them. Instead, they must use the command `/betterreload:reload`.

# How To Use? (Server Owner)

Simply install the jar file in the latest release labeled as "plugin".

# How To Use? (Developer)

All releases of BetterReload are available through the [JitPack](https://jitpack.io/#amnoah/betterreload) package repository.

If you are hoping to add support for the ReloadEvent, select the [API-v1.0.0](https://jitpack.io/#amnoah/betterreload/API-v1.0.0)
release on JitPack and follow the instructions on how to add it to your project. Then, register a Bukkit event listener
with a ReloadEvent handler inside it as you would do with any other event - it's that easy! An example is available in
the example module of this project.

If you are hoping to use the plugin's ReloadManager, select the latest Plugin release on JitPack and follow the
instructions on how to add it to your project. You should then be able to access the ReloadManager class.

Don't forget to add BetterReload as a dependency/soft-dependency (depending on your usage)!

# Plugins That Support BetterReload

- AnimeBoard by JustDoom | [GitHub](https://github.com/JustDoom/AnimeBoard)
- BetterDefusal by am noah | [Modrinth](https://modrinth.com/plugin/betterdefusal) | [GitHub](https://github.com/amnoah/BetterDefusal)
- Better Messages by JustDoom | [Modrinth](https://modrinth.com/plugin/bettermessages) | [GitHub](https://github.com/JustDoom/Better-Messages)

If you would like your plugin featured here, make an issue on the BetterReload GitHub page with the "Add Supported Plugin"
label. Make sure to include the name of the plugin and a link to the page you would like people directed to.

# Support

For general support, please join my [Discord server](https://discord.gg/ey9uTg3hcy).

For issues with the project, please open an issue in the issues tab.
