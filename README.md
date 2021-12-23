# Royal Grenadier
Implementation of Mojang's Brigadier for PaperMC

**Requires Java 11+ and Paper**

Mojang created a command engine called Brigadier that's been in use since 1.13. Neither Spigot nor Paper have implemented this engine, so I've done it for them lol.

###### Initially created for the ForTheCrown minecraft server

## Using Grenadier
Using Grenadier is easy. 

Download the remapped jar and add it to your dependencies. Then go to the [Brigadier Repo](https://github.com/Mojang/brigadier) and get Mojang's Brigadier as well.

There is no maven repository for Grenadier at the moment.

To learn how to create commands with Grenadier, check out the [Wiki](https://github.com/BotulToxin/RoyalGrenadier/wiki/Creating-a-command)

After you've created a plugin with Grenadier you can either shade the remapped standalone jar into your plugin, or use the grenadier plugin on your server.
