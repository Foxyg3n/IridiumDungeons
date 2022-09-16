package me.foxyg3n.iridiumdungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.configs.ResourcePack;

public class PlayerJoinListener implements Listener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ResourcePack resourcePack = IridiumDungeons.getInstance().getResourcePack();
        if(resourcePack.resourcePackUrl != null && !resourcePack.resourcePackUrl.equals("insert-url")) {
            e.getPlayer().setResourcePack(resourcePack.resourcePackUrl, null, "Proszę zaakceptuj resource pack, aby grać na BlackMc Skyblock", true);
        }
    }

}
