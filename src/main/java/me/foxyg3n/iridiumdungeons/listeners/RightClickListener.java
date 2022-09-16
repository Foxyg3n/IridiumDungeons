package me.foxyg3n.iridiumdungeons.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;

import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.DungeonType;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.configs.FreePasses;
import me.foxyg3n.iridiumdungeons.utils.MessageUtils;

public class RightClickListener implements Listener {

    private static Map<UUID, Integer> chestSettingPlayers = new HashMap<UUID, Integer>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(event.getHand() == null) return;
        if(!event.getHand().equals(EquipmentSlot.HAND)) return;
        
        if(event.hasItem() && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            ItemStack item = event.getItem();
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if(data.has(new NamespacedKey(IridiumDungeons.getInstance(), "dungeonBypass"), PersistentDataType.STRING) || item.getItemMeta().getDisplayName().startsWith("\u00A75\u00A7lPrzepustka \u00A77-")) {
                Player player = event.getPlayer();
                DungeonType type;
                if(data.has(new NamespacedKey(IridiumDungeons.getInstance(), "dungeonBypass"), PersistentDataType.STRING)) {
                    String bypassType = data.get(new NamespacedKey(IridiumDungeons.getInstance(), "dungeonBypass"), PersistentDataType.STRING);
                    type = switch(bypassType) {
                        case "EASY" -> DungeonType.EASY;
                        case "MEDIUM" -> DungeonType.MEDIUM;
                        case "HARD" -> DungeonType.HARD;
                        default -> DungeonType.EASY;
                    };
                } else {
                    if(item.getItemMeta().getDisplayName().endsWith("\u00A75\u00A7lŁatwy Dungeon")) {
                        type = DungeonType.EASY;
                    } else if(item.getItemMeta().getDisplayName().endsWith("\u00A75\u00A7lŚredni Dungeon")) {
                        type = DungeonType.MEDIUM;
                    } else if(item.getItemMeta().getDisplayName().endsWith("\u00A75\u00A7lTrudny Dungeon")) {
                        type = DungeonType.HARD;
                    } else {
                        return;
                    }
                }
                FreePasses freePasses = IridiumDungeons.getInstance().getFreePasses();
                if(!freePasses.hasFreePass(player, type)) {
                    freePasses.enableFreePass(player, type);
                    MessageUtils.sendMessage(player, "&aUruchomiłeś przepustkę na " + ChatColor.YELLOW + type.getName());
                    item.setAmount(item.getAmount() - 1);
                } else {
                    MessageUtils.sendMessage(player, "&cTwoja przepustka na " + ChatColor.YELLOW + type.getName() + "&c jest już aktywowana");
                }
                event.setCancelled(true);
            }
        }

        //"\u00A75\u00A7lPrzepustka \u00A77- \u00A75\u00A7lTrudny Dungeon"

        if(!chestSettingPlayers.containsKey(event.getPlayer().getUniqueId()) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().getDungeonById(chestSettingPlayers.get(event.getPlayer().getUniqueId()));
        Block block = event.getClickedBlock();
        Location lootChestLocation = block.getLocation().add(0.5, 0, 0.5);
        lootChestLocation.setPitch(Math.round(event.getPlayer().getLocation().getPitch() / 45) * 45 + 180);
        dungeon.addLootChest(lootChestLocation);
        event.getPlayer().sendMessage(StringUtils.color("%prefix% &7You've set a loot chest position!".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        chestSettingPlayers.remove(event.getPlayer().getUniqueId());
        event.setCancelled(true);
    }

    public static void addSettingPlayer(Player player, int id) {
        chestSettingPlayers.put(player.getUniqueId(), id);
    }

}
