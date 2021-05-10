package inv.me.own;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class GUI {
    public static void openDoorsInventory(Player p, String doorID, String doorName) {
        Inventory inv = Bukkit.createInventory(null, 27, "PICK ITEMS");
        NamespacedKey key = NamespacedKey.minecraft(doorID.toLowerCase());
        {
            ItemStack item = new ItemStack(Material.IRON_DOOR);
            ItemMeta mitem = item.getItemMeta();
            mitem.setDisplayName("§eID: §f" + doorID);
            mitem.setLore(Arrays.asList("§eName: §f" + doorName));
            item.setItemMeta(mitem);
            inv.setItem(11, item);
        }
        {
            ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
            ItemMeta mitem = item.getItemMeta();
            mitem.setDisplayName("§eID: §f" + doorID);
            mitem.setLore(Arrays.asList("§eName: §f" + doorName));
            item.setItemMeta(mitem);
            inv.setItem(15, item);
        }
        {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta mitem = item.getItemMeta();
            mitem.setDisplayName(Config.getKeyName(doorID, doorName));
            mitem.getPersistentDataContainer().set(key, PersistentDataType.STRING, "marker");
            item.setItemMeta(mitem);
            inv.setItem(13, item);
        }
        p.openInventory(inv);
    }
}
