# BetterReload

Let's be honest, Bukkit's reload command is based on a flawed idea which often ends up causing more problems than
benefits. It's to the point where forks like Spigot and Paper have added messages warning users against the use of the
command, telling them to rather restart the server. So, what if the reload command was replaced entirely by a better
system?

Inspired by the [SpongePowered](https://github.com/SpongePowered/Sponge) project, BetterReload swaps Bukkit's attempt
at literally reloading all plugins to an event that is passed along to plugins instead. Upon issuing the /reload
command with BetterReload installed, plugins that depend on BetterReload can receive an event that leaves the reloading
process up to the plugins.

# How Does It Work?

As far as I can tell you cannot simply override a Bukkit command, leading to the decision to instead alter the command 
before it is fully processed. When the command `/reload` is sent through the server, it is swapped to `/betterreload:reload`
before being processed.

This method also allows Bukkit's `/reload` command to be accessible through `/bukkit:reload`, although I would personally
advise against its usage regardless.

# How To Use? (Server Owner)

Simply install the jar file in the latest release labeled as "plugin".

# How To Use? (Developer)

You can add BetterReload to your project using [JitPack](https://jitpack.io/#amnoah/betterreload/API-v1.0.0). Select the
dependency system you're using and copy the repository/dependency settings into your project. From there, just reload
your dependencies and you should have BetterReload accessible from your project.

The example module of this project demonstrates this process in Maven as well as actual usage of the ReloadEvent.

# Issues

- `BlockCommandSender`s (typically command blocks) cannot use the `/reload` command as there is no accessible preprocessing
stage for them. Instead, they must use the command `/betterreload:reload`.

# Command Usage

- Use `/reload` to send a reload event to plugins.
- Any arguments put past the command will be recognized as plugins to specifically send the event to.
- The permission node is `better.reload`.
- Example: `/reload` will send a reload event to all plugins with support built in.
- Example: `/reload plugin1` will send the reload event to only a plugin with the name plugin1.
- Example: `/reload plugin1 plugin2` will send the reload event to only a plugin with the name plugin1 and a plugin with the name plugin2.

# Plugins That Support BetterReload

- Better Messages by JustDoom | [Spigot](https://www.spigotmc.org/resources/better-messages.82830/) | [GitHub](https://github.com/JustDoom/Better-Messages)

If you would like your plugin featured here, make an issue on the BetterReload GitHub page with the "Add Supported Plugin"
label. Make sure to include the name of the plugin and a link to the page you would like people directed to.
