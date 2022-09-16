package me.foxyg3n.iridiumdungeons.listeners;

import java.time.Duration;
import java.util.HashMap;
import java.util.stream.Stream;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.lumine.mythic.bukkit.BukkitAdapter;
import me.foxyg3n.foxlib.providers.CooldownProvider;
import me.foxyg3n.iridiumdungeons.DungeonType;

public class EntityDamageByEntityListener implements Listener {

    public static HashMap<DungeonType, CooldownProvider<Player>> dungeonBossHitters = new HashMap<DungeonType, CooldownProvider<Player>>();
    
    private static final long HIT_COOLDOWN = 20;

    static {
        Stream.of(DungeonType.values()).forEach(type -> dungeonBossHitters.put(type, new CooldownProvider<>(Duration.ofSeconds(HIT_COOLDOWN))));
    }
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        Entity entity = event.getEntity();
        Player player = (Player) event.getDamager();
        DungeonType type =
            hasMetadata(entity, "easyDungeonBoss") ? DungeonType.EASY :
            hasMetadata(entity, "mediumDungeonBoss") ? DungeonType.MEDIUM :
            hasMetadata(entity, "hardDungeonBoss") ? DungeonType.HARD : null;
        if(type == null) return;
        dungeonBossHitters.get(type).applyCooldown(player);
    }

    private boolean hasMetadata(Entity entity, String key) {
        return BukkitAdapter.adapt(entity).hasMetadata(key);
    }

}
