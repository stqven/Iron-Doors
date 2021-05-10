package inv.me.own;

import inv.me.own.Main;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInteractListener implements Listener {
    private Main plugin;

    public PlayerInteractListener(Main plugin) {
        this.plugin = plugin;
    }

    public boolean canBuild(Main plugin, Player player, Location location, ItemStack itemStack) {
        itemStack = (itemStack == null) ? new ItemStack(Material.AIR) : itemStack;
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(location.getBlock(), location.getBlock().getState(), location.getBlock(), itemStack, player, true, EquipmentSlot.HAND);
        plugin.getServer().getPluginManager().callEvent((Event)blockPlaceEvent);
        return blockPlaceEvent.canBuild();
    }

    public void toggleDoor(Main plugin, Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Openable) {
            Openable openable = (Openable)blockData;
            openable.setOpen(!openable.isOpen());
            block.setBlockData((BlockData)openable);
            if (blockData instanceof org.bukkit.block.data.type.Door) {
                block.getWorld().playEffect(block.getLocation(), Effect.IRON_DOOR_TOGGLE, 0);
            } else if (blockData instanceof org.bukkit.block.data.type.TrapDoor) {
                block.getWorld().playEffect(block.getLocation(), Effect.IRON_TRAPDOOR_TOGGLE, 0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Inventory inv = player.getInventory();
        if (block != null &&
                !player.isSneaking() &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK && (block.getType() == Material.IRON_DOOR || block.getType() == Material.IRON_TRAPDOOR) &&
                canBuild(this.plugin, player, block.getLocation(), event.getPlayer().getItemInHand()) &&
                event.getHand() == EquipmentSlot.HAND && Keys.checkKey(player.getInventory().getItemInMainHand(), block.getLocation(), player)) {
                toggleDoor(this.plugin, block);

            if (event.isBlockInHand() || (event.getHand() == EquipmentSlot.OFF_HAND))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if (b.getType() == Material.IRON_DOOR || b.getType() == Material.IRON_TRAPDOOR) {
            ItemStack hand = (p.getInventory().getItemInMainHand() != null && (p.getInventory().getItemInMainHand().getType() == Material.IRON_TRAPDOOR || p.getInventory().getItemInMainHand().getType() == Material.IRON_DOOR))? p.getInventory().getItemInMainHand() : p.getInventory().getItemInOffHand();
            if (hand == null || hand.getType() == Material.AIR) return;
                Location loc = b.getLocation();
                if (hand.hasItemMeta() && hand.getItemMeta().getDisplayName().startsWith("§eID: §f") && hand.getItemMeta().hasLore() && hand.getItemMeta().getLore().get(0).startsWith("§eName: §f")) {
                    String doorID = hand.getItemMeta().getDisplayName().replace("§eID: §f", "");
                    String doorName = hand.getItemMeta().getLore().get(0).replace("§eName: §f", "");
                    if (Config.addDoor(doorID.toLowerCase(), doorName, loc)) {
                        p.sendMessage(Config.getDoorCreatedMessage(doorID, doorName));
                    } else {
                        p.sendMessage(Config.getDoorAlreadyMessage(doorID, doorName));
                    }
                }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if (b.getType() == Material.IRON_DOOR || b.getType() == Material.IRON_TRAPDOOR) {
            if (p.isOp()) {
                Location loc = b.getLocation();
                for (String doorID : Keys.keys.values()) {
                    if (Config.containsDoor(doorID, loc, true)) {
                        p.sendMessage(Config.getDoorRemovedMessage(doorID, Keys.getDoorName(doorID)));
                        return;
                    }
                }
            } else {
                e.setCancelled(true);
            }
        }
    }
}
