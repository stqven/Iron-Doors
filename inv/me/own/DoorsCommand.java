package inv.me.own;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoorsCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("doors")) {
            Player p = (Player) sender;
            ArrayList<String> helpMsg = Config.getHelpCommands();
            if (args.length == 0) {
                helpMsg.forEach(msg -> p.sendMessage(msg));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    p.sendMessage(helpMsg.get(0));
                } else if (args[0].equalsIgnoreCase("giveKey")) {
                    p.sendMessage(helpMsg.get(1));
                } else if (args[0].equalsIgnoreCase("setName")) {
                    p.sendMessage(helpMsg.get(2));
                } else if (args[0].equalsIgnoreCase("list")) {
                    showDoorsList(p);
                } else {
                    helpMsg.forEach(msg -> p.sendMessage(msg));
                }
            } else {
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("add"))
                        p.sendMessage(helpMsg.get(0));
                    else if (args[0].equalsIgnoreCase("giveKey"))
                        p.sendMessage(helpMsg.get(1));
                    else if (args[0].equalsIgnoreCase("setName"))
                        p.sendMessage(helpMsg.get(2));
                    else if (args[0].equalsIgnoreCase("setName"))
                        showDoorsList(p);
                    return false;
                }
                String doorID = args[1];
                String doorName = "";
                for (int i = 2; i < args.length; i++) {
                    if (i != 2) doorName += " ";
                    doorName += args[i];
                }
                if (doorName.toCharArray().length > 16) {
                    p.sendMessage(Config.getMaxLengthMessage());
                    return false;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (Config.containsDoor(doorID, null, false)) {
                        GUI.openDoorsInventory(p, doorID, Keys.getDoorName(doorID));
                    } else {
                        GUI.openDoorsInventory(p, doorID, doorName);
                    }
                } else if (args[0].equalsIgnoreCase("giveKey")) {
                    if (Config.containsDoor(doorID, null, false)) {
                        String targetName = args[2];
                        Player t = Bukkit.getPlayer(targetName);
                        ItemStack item = new ItemStack(Material.PAPER);
                        ItemMeta mitem = item.getItemMeta();
                        mitem.setDisplayName(Config.getKeyName(doorID, doorName));
                        mitem.getPersistentDataContainer().set(NamespacedKey.minecraft(doorID.toLowerCase()), PersistentDataType.STRING, "marker");
                        item.setItemMeta(mitem);
                        if (t != null) {
                            t.getInventory().addItem(item);
                            p.sendMessage(Config.getKeyGivenMessage(doorID, doorName, targetName));
                            if (!p.equals(p)) {
                                t.sendMessage(Config.getKeyReceivedMessage(doorID, doorName, p.getName()));
                            }
                        } else {
                            p.sendMessage(Config.getPlayerOfflineMessage(targetName));
                        }
                    } else {
                        p.sendMessage(Config.getDoorNotFoundMessage(doorID));
                    }
                } else if (args[0].equalsIgnoreCase("setName")) {
                    int counter = 1;
                    FileConfiguration cfg = Main.getInstance().getConfig();
                    while (cfg.contains(counter + ".ID")) {
                        if (cfg.getString(counter + ".ID").equals(doorID)) {
                            cfg.set(counter + ".Name", doorName);
                            Main.getInstance().saveConfig();
                            p.sendMessage(Config.getNameChangedMessage(doorID, doorName));
                            return false;
                        }
                        counter++;
                    }
                    p.sendMessage(Config.getDoorNotFoundMessage(doorID));
                } else if (args[0].equalsIgnoreCase("list")) {
                    showDoorsList(p);
                } else {
                    helpMsg.forEach(msg -> p.sendMessage(msg));
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("doors")) {
            if (args.length == 1) {
                return Arrays.asList("add,giveKey,setName".split(","));
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("giveKey") || args[0].equalsIgnoreCase("setName"))
                return Config.getDoorsIDs();
                else return new ArrayList<>();
            } else {
                return null;
            }
        }
        return null;
    }

    public static void showDoorsList(Player p) {
        if (!Keys.keys.values().isEmpty()) {
            p.sendMessage("§bCurrent added doors:");
            for (String key : Keys.keys.values()) {
                p.sendMessage("§8- §e" + key + "§f: " + Keys.getDoorName(key));
            }
        } else {
            p.sendMessage("§cYou have not added any doors yet!");
        }
    }
}
