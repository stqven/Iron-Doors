package inv.me.own;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Keys {
    static HashMap<NamespacedKey, String> keys = new HashMap<>();

    public static void loadKeys() {
        keys.clear();
        int counter = 1;
        FileConfiguration cfg = Main.getInstance().getConfig();
        while (cfg.contains(counter + ".ID")) {
            String doorID = cfg.getString(counter + ".ID");
            keys.put(NamespacedKey.minecraft(doorID), doorID);
            counter++;
        }
    }

    public static ArrayList<String> getKeys() {
        return (ArrayList<String>) keys.values();
    }

    public static String getDoorName(String doorID) {
        int counter = 1;
        FileConfiguration cfg = Main.getInstance().getConfig();
        while (cfg.contains(counter + ".ID")) {
            if (cfg.getString(counter + ".ID").equalsIgnoreCase(doorID)) {
                return cfg.getString(counter + ".Name");
            }
            counter++;
        }
        return null;
    }

    public static boolean checkKey(ItemStack item, Location loc, Player p) {
        for (NamespacedKey k : keys.keySet()) {
            if (item.getItemMeta().getPersistentDataContainer().has(k, PersistentDataType.STRING)) {
                int counter = 1;
                while (Main.getInstance().getConfig().contains(counter + ".ID")) {
                    if (Main.getInstance().getConfig().getString(counter + ".ID").equals(keys.get(k))) {
                        List<String> locs = Main.getInstance().getConfig().getStringList(counter + ".Locations");
                        if (locs.contains(loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()) || locs.contains(loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + (loc.getBlockY() - 1) + ", " + loc.getBlockZ())) {
                            return true;
                        }
                    };
                    counter++;
                }
                p.sendMessage(Config.getWrongKeyMessage());
                break;
            }
        }
        return false;
    }
}
