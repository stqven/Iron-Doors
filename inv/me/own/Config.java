package inv.me.own;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {

    private static FileConfiguration cfg = Main.getInstance().getConfig();

    // Utilities

    private static String getConfigString(String dir) {
        return cfg.contains(dir)? cfg.getString(dir) : null;
    }

    private static ArrayList<String> getConfigList(String dir) {
        return cfg.contains(dir)? (ArrayList<String>) cfg.getStringList(dir) : new ArrayList<String>();
    }

    private static String translateCodes(String str, String doorID, String doorName, String player) {
        String nstr = str;
        nstr = str.replaceAll("%prefix%", getPrefix());
        nstr = nstr.replaceAll("&", "§");
        if (doorID != null) {
            nstr = nstr.replaceAll("%door_id%", doorID);
        }
        if (doorName != null) {
            nstr = nstr.replaceAll("%door_name%", doorName);
        }
        if (player != null) {
            nstr = nstr.replaceAll("%player%", player);
        }
        return nstr;
    }

    private static ArrayList<String> translateCodes(List<String> list, String doorID, String doorName, String player) {
        ArrayList<String> nlist = new ArrayList<>();
        for (String item : list) {
            nlist.add(translateCodes(item, doorID, doorName, player));
        }
        return nlist;
    }

    // Messages

    private static String getPrefix() {
        return getConfigString("Messages.prefix");
    }

    public static ArrayList<String> getHelpCommands() {
        return translateCodes(getConfigList("Messages.help-messages"), null, null, null);
    }

    public static String getMaxLengthMessage() {
        return translateCodes(getConfigString("Messages.name-max-length"), null, null, null);
    }

    public static String getNameChangedMessage(String doorID, String doorName) {
        return translateCodes(getConfigString("Messages.name-changed"), doorID, doorName, null);
    }

    public static String getDoorRemovedMessage(String doorID, String doorName) {
        return translateCodes(getConfigString("Messages.door-removed"), doorID, doorName, null);
    }

    public static String getDoorCreatedMessage(String doorID, String doorName) {
        return translateCodes(getConfigString("Messages.door-created"), doorID, doorName, null);
    }

    public static String getDoorAlreadyMessage(String doorID, String doorName) {
        return translateCodes(getConfigString("Messages.door-already-exist"), doorID, doorName, null);
    }

    public static String getDoorNotFoundMessage(String doorID) {
        return translateCodes(getConfigString("Messages.door-not-found"), doorID, null, null);
    }

    public static String getKeyGivenMessage(String doorID, String doorName, String player) {
        return translateCodes(getConfigString("Messages.key-given"), doorID, doorName, player);
    }

    public static String getKeyReceivedMessage(String doorID, String doorName, String player) {
        return translateCodes(getConfigString("Messages.key-received"), doorID, doorName, player);
    }

    public static String getKeyName(String doorID, String doorName) {
        return translateCodes(getConfigString("Messages.key-name"), doorID, doorName, null);
    }

    public static String getPlayerOfflineMessage(String player) {
        return translateCodes(getConfigString("Messages.player-offline"), null, null, player);
    }

    public static String getWrongKeyMessage() {
        return translateCodes(getConfigString("Messages.wrong-key"), null, null, null);
    }

    // M.U
    public static boolean containsDoor(String doorID, Location loc, boolean remove) {
        int counter = 1;
        FileConfiguration cfg = Main.getInstance().getConfig();
        while (cfg.contains(counter + ".ID")) {
            if (cfg.getString(counter + ".ID").equals(doorID)) {
                if (loc == null) return true;
                ArrayList<String> list = (ArrayList<String>) cfg.getList(counter + ".Locations");
                for (String str : list) {
                    if ((str.equals(loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ())) || str.equals(loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + (loc.getBlockY() - 1) + ", " + loc.getBlockZ())) {
                        if (remove) {
                            list.remove(str);
                            cfg.set(counter + ".Locations", list);
                            Main.getInstance().saveConfig();
                        }
                        return true;
                    }
                }
            }
            counter++;
        }
        return false;
    }

    public static List<String> getDoorsIDs() {
        int counter = 1;
        List<String> list = new ArrayList<>();
        FileConfiguration cfg = Main.getInstance().getConfig();
        while (cfg.contains(counter + ".ID")) {
            list.add(cfg.getString((counter++) + ".ID"));
        }
        return list;
    }

    public static boolean addDoor(String doorID, String doorName, Location loc) {
        if (containsDoor(doorID, loc, false)) return false;
        FileConfiguration cfg = Main.getInstance().getConfig();
        int counter = 1;
        while (cfg.contains(counter + ".ID")) {
            if (cfg.getString(counter + ".ID").equals(doorID)) {
                ArrayList<String> list = (ArrayList<String>) cfg.getStringList(counter  + ".Locations");
                list.add(loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                cfg.set(counter + ".Locations", list);
                Main.getInstance().saveConfig();
                return true;
            }
            counter++;
        }
        counter = getCounter();
        ArrayList<String> list = new ArrayList<>();
        list.add(loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
        cfg.set(counter + ".ID", doorID);
        cfg.set(counter + ".Name", doorName);
        cfg.set(counter + ".Locations", list);
        Main.getInstance().saveConfig();
        Keys.loadKeys();
        return true;
    }

    public static void setDoorName(String doorID, String doorName) {
        int counter = 1;
        FileConfiguration cfg = Main.getInstance().getConfig();
        while (cfg.contains(counter + ".ID")) {
            if (cfg.getString(counter + ".ID").equals(doorID)) {
                cfg.set(counter + ".Name", doorName);
                Main.getInstance().saveConfig();
            }
        }
    }

    public static int getCounter() {
        int counter = 1;
        FileConfiguration cfg = Main.getInstance().getConfig();
        while (cfg.contains(counter + ".ID")) {
            counter++;
        }
        return counter;
    }
}
