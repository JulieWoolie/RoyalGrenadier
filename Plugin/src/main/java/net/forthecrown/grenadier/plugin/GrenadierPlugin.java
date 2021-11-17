package net.forthecrown.grenadier.plugin;

import net.forthecrown.royalgrenadier.RoyalGrenadier;
import net.forthecrown.royalgrenadier.TestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class GrenadierPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        RoyalGrenadier.initialize();

        new TestCommand(this);
    }
}
