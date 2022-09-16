package me.foxyg3n.iridiumdungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class MythicMobsReloadListener implements Listener {

    @EventHandler
    public void onMythicMobsReload(MythicReloadedEvent event) {
        IridiumDungeons.getInstance().loadCustomConfigs();
    }

}
