package me.foxyg3n.iridiumdungeons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class PlayerDeathListener implements Listener {
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().getPlayingPlayerDungeon(player);
        if(dungeon != null) {
            dungeon.removePlayer(player);
        }
    }

}
