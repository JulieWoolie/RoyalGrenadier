package net.forthecrown.royalgrenadier;

import net.forthecrown.grenadier.RoyalGrenadier;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PluginMain extends JavaPlugin {
    public static Logger LOGGER;

    //:I I didn't wanna have this be a plugin, but I also couldn't
    //have a jar sitting around in the plugins folder lol
    @Override
    public void onLoad() {
        LOGGER = getLogger();

        RoyalGrenadier.init();
        RoyalArgumentRegistry.init();

        //new TestCommand(this);
    }
}
