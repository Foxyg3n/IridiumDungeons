package me.foxyg3n.iridiumdungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.api.IridiumSkyblockReloadEvent;

import me.foxyg3n.iridiumdungeons.commands.DungeonCommand;
import me.foxyg3n.iridiumdungeons.commands.TestCommand;

public class IridiumSkyblockReloadListener implements Listener {
    
    @EventHandler
    public void onReload(IridiumSkyblockReloadEvent e) {
        IridiumSkyblockAPI.getInstance().addCommand(new DungeonCommand());
        IridiumSkyblockAPI.getInstance().addCommand(new TestCommand());
    }

}
