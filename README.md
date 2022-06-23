# Royal Grenadier
Implementation of Mojang's Brigadier for PaperMC

**Requires Java 11+ and Paper**

Mojang created a command engine called Brigadier that's been in use since 1.13. Neither Spigot nor Paper have implemented this engine, so I've done it for them lol.

###### Initially created for the ForTheCrown minecraft server

## Using Grenadier
Using Grenadier is easy.

If you're using a mojang mappings to write your plugin, download the jar file ending with `-dev` otherwise, download the normal jar.  
Make sure you also have Mojang's Brigadier as well, you can get that [here!](https://github.com/Mojang/brigadier#gradle)

If you're shading Grenadier into your own plugin, make sure to call `RoyalGrenadier.initialize(Plugin)` in your `onEnable()`, like so:
```java
public class Example extends JavaPlugin {
	@Override
	public void onEnable() {
		RoyalGrenadier.initialize(this);
	}
}
```
If you're using the Grenadier plugin, just add `RoyalGrenadier` as a depend in your plugin.yml

You can go [to the wiki here](https://github.com/BotulToxin/RoyalGrenadier/wiki/Creating-a-command) to see how to create a simple command to get you started