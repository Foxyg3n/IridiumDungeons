package me.foxyg3n.iridiumdungeons.listeners;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.drops.DropTable;
import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.configs.data.BossType;
import me.foxyg3n.iridiumdungeons.database.DungeonManager;
import me.foxyg3n.iridiumdungeons.events.DungeonBeatEvent;

public class MythicMobDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent event) {
        AbstractEntity entity = event.getMob().getEntity();
        Optional<MythicMob> lootChestOptional = IridiumDungeons.getInstance().getDungeonManager().getLootChest();
        DungeonManager dungeonManager = IridiumDungeons.getInstance().getDungeonManager();
        Optional<DropTable> drop;
        if(lootChestOptional.isPresent() && event.getMob().getType().equals(lootChestOptional.get())) {
            if(dungeonManager.easyDungeon.containsLocation(BukkitAdapter.adapt(entity.getLocation()))) {
                drop = IridiumDungeons.getInstance().getLoots().easyDungeonLoot;
            } else if(dungeonManager.mediumDungeon.containsLocation(BukkitAdapter.adapt(entity.getLocation()))) {
                drop = IridiumDungeons.getInstance().getLoots().mediumDungeonLoot;
            } else if(dungeonManager.hardDungeon.containsLocation(BukkitAdapter.adapt(entity.getLocation()))) {
                drop = IridiumDungeons.getInstance().getLoots().hardDungeonLoot;
            } else {
                drop = Optional.empty();
            }
        } else {
            drop = Optional.empty();
        }
        if(drop.isPresent()) {
            Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> drop.get().generate().drop(entity.getLocation()), 20);
            return;
        }
        Dungeon dungeon =
            entity.getName().endsWith(BossType.EASY.getName()) ? dungeonManager.easyDungeon :
            entity.getName().endsWith(BossType.MEDIUM.getName()) ? dungeonManager.mediumDungeon :
            entity.getName().endsWith(BossType.HARD.getName()) ? dungeonManager.hardDungeon : null;
        if(dungeon == null) return;
        dungeon.broadcastMessage("Boss został pokonany! Pozbieraj resztę lootu lub opuść dungeon za pomocą /is dungeons opusc");
        Optional<DropTable> dropTable = switch(dungeon.getType()) {
            case EASY -> IridiumDungeons.getInstance().getLoots().easyDungeonBossLoot;
            case MEDIUM -> IridiumDungeons.getInstance().getLoots().mediumDungeonBossLoot;
            case HARD -> IridiumDungeons.getInstance().getLoots().hardDungeonBossLoot;
        };
        if(!dropTable.isPresent()) return;
        EntityDamageByEntityListener.dungeonBossHitters.get(dungeon.getType()).getCooldowns().forEach(player -> dropTable.get().generate().equipOrGive(BukkitAdapter.adapt(player)));
        Bukkit.getPluginManager().callEvent(new DungeonBeatEvent(dungeon));
    }
    
}
