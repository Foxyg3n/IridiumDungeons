package me.foxyg3n.iridiumdungeons;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.gui.GUI;

public class DungeonGUI extends GUI {

    private Player player;

    public DungeonGUI(Player player) {
        this(player, player.getOpenInventory().getTopInventory());
    }

    public DungeonGUI(Player player, Inventory previousInventory) {
        super(IridiumDungeons.getInstance().getInventories().dungeonsGUI, previousInventory);
        this.player = player;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        InventoryUtils.fillInventory(inventory, getNoItemGUI().background);

        for(Dungeon dungeon : IridiumDungeons.getInstance().getDungeonManager().getDungeonList()) {
            long minutes = LocalDateTime.now().until(dungeon.getTime(), ChronoUnit.MINUTES);
            long seconds = LocalDateTime.now().until(dungeon.getTime(), ChronoUnit.SECONDS) - minutes * 60;
            inventory.setItem(dungeon.item.slot, ItemStackUtils.makeItem(dungeon.item, Arrays.asList(
                new Placeholder("dungeon_state", dungeon.isStopped() ? ChatColor.RED + "Tymczasowo zawieszony" : dungeon.isRunning() ? "W trakcie" : "Oczekiwanie..."),
                new Placeholder("dungeon_playercount", dungeon.isRunning() ? dungeon.getPlayers().size() + "" : dungeon.getAwaitingPlayers().size() + ""),
                new Placeholder("timer_minutes", String.valueOf(Math.max(minutes, 0))),
                new Placeholder("timer_seconds", String.valueOf(Math.max(seconds, 0))),
                new Placeholder("vaultcost", IridiumSkyblock.getInstance().getNumberFormatter().format(dungeon.vaultCost)),
                new Placeholder("hasfreepass", IridiumDungeons.getInstance().getFreePasses().hasFreePass(player, dungeon.getType()) ? "&9(Posiadasz darmową przepustkę)" : "")
            )));
        }

        Dungeon playerDungeon = IridiumDungeons.getInstance().getDungeonManager().getRegisteredPlayerDungeon(player);
        Item statusItem = getStatusItem(playerDungeon);
        inventory.setItem(statusItem.slot, ItemStackUtils.makeItem(statusItem, playerDungeon == null ? Collections.emptyList() : Arrays.asList(
            new Placeholder("dungeon_name", playerDungeon.item.displayName),
            new Placeholder("timer_minutes", String.valueOf(Math.max(playerDungeon.getRemainingTime() / 60, 0))),
            new Placeholder("timer_seconds", String.valueOf(Math.max(playerDungeon.getRemainingTime() % 60, 0)))
        )));

        Item leaveItem = new Item(XMaterial.OAK_DOOR, 16, 1, "&7Kliknij, aby wypisać się z dungeonu", Collections.emptyList());
        inventory.setItem(leaveItem.slot, ItemStackUtils.makeItem(leaveItem));
    }

    private Item getStatusItem(Dungeon dungeon) {
        Item statusItem;
        if(dungeon != null) {
            statusItem = new Item(XMaterial.GREEN_STAINED_GLASS_PANE, 17, 1, "Zapisałeś się na %dungeon_name%", Arrays.asList(
                "Jesteś zapisany na dungeon, który startuje za %timer_minutes%m %timer_seconds%s"
            ));
        } else {
            statusItem = new Item(XMaterial.RED_STAINED_GLASS_PANE, 17, 1, "&7Nie jesteś aktualnie zapisany w żadnym dungeonie", Arrays.asList(
                "Może do jakiegoś dołącz?"
            ));
        }
        return statusItem;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        for(Dungeon dungeon : IridiumDungeons.getInstance().getDungeonManager().getDungeonList()) {
            if(event.getSlot() == dungeon.item.slot) {
                if(IridiumDungeons.getInstance().getDungeonManager().getPlayingPlayerDungeon(player) != null) {
                    event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7Nie możesz się zapisać na dungeon jeżeli jesteś już zapisany w innym".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    return;
                }
                User user = IridiumSkyblock.getInstance().getUserManager().getUserByUUID(player.getUniqueId()).orElse(null);
                if(user == null) {
                    event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &cCoś poszło nie tak!".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    return;
                }
                // can afford the dungeon? has pass?
                if(!(IridiumSkyblock.getInstance().getEconomy().getBalance(player) >= dungeon.vaultCost) && !IridiumDungeons.getInstance().getFreePasses().hasFreePass(player, dungeon.getType())) {
                    event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7Nie posiadasz wystarczająco monet, aby zapisać się do tego dungeona".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    return;
                }
                // is player already registered?
                if(dungeon.isRegistered(player)) {
                    event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7Jesteś już zapisany w tym dungeonie".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    return;
                }
                // is there already max players?
                if(dungeon.getAwaitingPlayers().size() >= 4) {
                    event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7W tym dungeonie jest już maksymalna liczba graczy".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    return;
                }
                // is dungeon already running?
                if(!dungeon.addAwaitingPlayer(player)) {
                    event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7Nie możesz się zapisać do dungeona, który trwa lub jest zawieszony".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    return;
                }
                event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7Zapisałeś się do tego dungeona".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        }
        if(event.getSlot() == 16) {
            Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().getRegisteredPlayerDungeon(player);
            if(dungeon == null) return;
            if(dungeon.isRunning()) {
                dungeon.removePlayer(player);
            } else {
                dungeon.removeAwaitingPlayer(player);
            }
            event.getWhoClicked().sendMessage(StringUtils.color("%prefix% &7Opuściłeś dungeon".replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }
    
}
