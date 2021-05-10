package inv.me.own;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;

    public void onEnable() {
        saveDefaultConfig();
        System.out.println("[DCards] The plugin has been enabled!");
        getServer().getPluginManager().registerEvents((Listener)new PlayerInteractListener(this), (Plugin)this);
        getCommand("doors").setExecutor(new DoorsCommand());
        getCommand("doors").setTabCompleter(new DoorsCommand());
        Keys.loadKeys();
    }

    public void onLoad() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

}
