package me.foxyg3n.iridiumdungeons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.database.DungeonManager;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DungeonManager dungeonManager = IridiumDungeons.getInstance().getDungeonManager();
        Dungeon dungeon =
                dungeonManager.easyDungeon.isInDungeon(player) ? dungeonManager.easyDungeon :
                dungeonManager.mediumDungeon.isInDungeon(player) ? dungeonManager.mediumDungeon :
                dungeonManager.hardDungeon.isInDungeon(player) ? dungeonManager.hardDungeon : null;
        if(dungeon == null) return;
        event.setRespawnLocation(dungeon.getSpawnLocation());
    }

}
