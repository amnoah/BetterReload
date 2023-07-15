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
before it is fully processed. When the command /reload is sent through the server, it is swapped to /BetterReload:reload
before being processed.

This method also allows Bukkit's /reload command to be accessible through /Bukkit:reload, although I would personally
advise against its usage regardless.