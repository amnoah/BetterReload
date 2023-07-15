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
before it is fully processed. When the command `/reload` is sent through the server, it is swapped to `/BetterReload:reload`
before being processed.

This method also allows Bukkit's `/reload` command to be accessible through `/Bukkit:reload`, although I would personally
advise against its usage regardless.

# How To Use? (Server Owner)

Simply install the latest jar file labeled as "plugin" in the releases tab.

# How To Use? (Developer)

You can add BetterReload to your project using [JitPack](https://jitpack.io/#amnoah/betterreload/). First, click the
"Get It" button alongside the latest version. Next, select the dependency system you're using. Lastly, click "Subproject"
and select "api". With these settings in place you can copy the repository and dependency settings into your project,
needing only a dependency reload to have BetterReload accessibly from your project.

The example module of this project demonstrates this process in Maven as well as actual usage of the ReloadEvent.

# Issues

- `BlockCommandSender`s (typically command blocks) cannot use the `/reload` command as there is no accessible preprocessing
stage for them. Instead, they must use the command `/BetterReload:reload`.