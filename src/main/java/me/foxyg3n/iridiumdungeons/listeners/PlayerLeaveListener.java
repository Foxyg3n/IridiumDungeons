package me.foxyg3n.iridiumdungeons.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class PlayerLeaveListener implements Listener {
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Dungeon dungeon = IridiumDungeons.getInstance().getDungeonManager().getPlayingPlayerDungeon(player);
        if(dungeon != null) {
            World world = IridiumDungeons.getInstance().getDefaultWorld();
            if(dungeon.isRunning()) {
                player.teleport(world.getSpawnLocation());
                dungeon.removePlayer(player);
            } else {
                dungeon.removeAwaitingPlayer(player);
            }
        }
    }

}
