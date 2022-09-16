package me.foxyg3n.iridiumdungeons;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BoundingBox;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.DatabaseObject;
import com.iridium.iridiumskyblock.IridiumSkyblock;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.foxyg3n.iridiumdungeons.configs.FreePasses;
import me.foxyg3n.iridiumdungeons.configs.data.BossType;
import me.foxyg3n.iridiumdungeons.configs.data.Position;
import me.foxyg3n.iridiumdungeons.database.converters.ItemConverter;
import me.foxyg3n.iridiumdungeons.database.converters.PositionConverter;
import me.foxyg3n.iridiumdungeons.database.converters.PositionListConverter;
import me.foxyg3n.iridiumdungeons.events.DungeonEndEvent;
import me.foxyg3n.iridiumdungeons.events.DungeonStartEvent;
import me.foxyg3n.iridiumdungeons.listeners.EntityDamageByEntityListener;
import me.foxyg3n.iridiumdungeons.utils.LocationUtil;
import me.foxyg3n.iridiumdungeons.utils.MessageUtils;
import net.milkbowl.vault.economy.Economy;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "dungeons")
public class Dungeon extends DatabaseObject {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "spawnLocation")
    @Convert(converter = PositionConverter.class)
    private Position spawnLocation;
    @Column(name = "bossCheckPos1")
    @Convert(converter = PositionConverter.class)
    private Position bossCheckPos1;
    @Column(name = "bossCheckPos2")
    @Convert(converter = PositionConverter.class)
    private Position bossCheckPos2;
    @Column(name = "areaPos1")
    @Convert(converter = PositionConverter.class)
    private Position areaPos1;
    @Column(name = "areaPos2")
    @Convert(converter = PositionConverter.class)
    private Position areaPos2;
    @Column(name = "bossSpawn")
    @Convert(converter = PositionConverter.class)
    private Position bossSpawn;
    @Column(name = "lootChests", length = 65555)
    @Convert(converter = PositionListConverter.class)
    private List<Position> lootChestPositions = new ArrayList<Position>();

    @Column(name = "name")
    private DungeonType type;
    @Column(name = "loaded")
    private boolean loaded = false;
    @Column(name = "breakDuration")
    private int breakDuration;
    @Column(name = "runDuration")
    private int runDuration;
    @Transient
    private long time = Instant.now().getEpochSecond();
    @Transient
    private boolean isRunning = false;
    @Transient
    private boolean isStopped = false;
    @Transient
    private boolean showLoot = false;

    @Column(name = "item", length = 65555)
    @Convert(converter = ItemConverter.class)
    public Item item;
    @Column(name = "vaultCost")
    public int vaultCost;

    @Transient
    private List<UUID> awaitingPlayers = new ArrayList<UUID>();
    @Transient
    private List<UUID> players = new ArrayList<UUID>();
    @Transient
    private List<ActiveMob> activeLootChests = new ArrayList<ActiveMob>();
    @Transient
    private ActiveMob activeBoss = null;
    @Transient
    private int bossTaskId = -1;
    @Transient
    private int runDungeonTaskId = -1;
    @Transient
    private int breakDungeonTaskId = -1;
    @Transient
    private int showLootTaskId = -1;

    public Dungeon(int id, DungeonType type, Item item, int vaultCost, int breakDuration, int runDuration) {
        this.id = id;
        this.type = type;
        this.item = item;
        this.vaultCost = vaultCost;
        this.breakDuration = breakDuration;
        this.runDuration = runDuration;
        this.isRunning = false;
        this.time = Instant.now().getEpochSecond();
        loaded = true;
    }

    public void runDungeon() {
        if(spawnLocation == null || bossCheckPos1 == null || bossCheckPos2 == null || bossSpawn == null || areaPos1 == null || areaPos2 == null) {
            Bukkit.getLogger().warning("Couldn't start dungeons, set all positions first and start dungeons manually.");
            this.isStopped = true;
            return;
        }
        if(isRunning || isStopped) return;
        this.isRunning = true;
        this.isStopped = false;
        setTime(LocalDateTime.now().plus(runDuration, ChronoUnit.MINUTES));
        cancelTasks();

        Economy economy = IridiumSkyblock.getInstance().getEconomy();
        FreePasses freePasses = IridiumDungeons.getInstance().getFreePasses();
        Location dungeonSpawn = getSpawnLocation();
        getAwaitingPlayers().forEach(player -> {
            if(freePasses.hasFreePass(player, type)) {
                freePasses.disableFreePass(player, type);
                MessageUtils.sendInfo(player, "Korzystasz z darmowej przepustki na " + ChatColor.YELLOW + type.getName());
            } else if(economy.getBalance(player) >= vaultCost) {
                economy.withdrawPlayer(player, vaultCost);
            } else {
                MessageUtils.sendWarning(player, "Nie masz wystarczającej ilości złota na " + ChatColor.YELLOW + type.getName());
                awaitingPlayers.remove(player.getUniqueId());
            }
        });

        players.addAll(awaitingPlayers);
        awaitingPlayers.clear();

        if(players.isEmpty()) {
            resetDungeon();
            return;
        }

        getPlayers().forEach(player -> {
            player.teleport(dungeonSpawn);
            MessageUtils.sendMessage(player, "Dungeon się rozpoczął! Udanych łowów");
        });

        Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> killEntities(), 60);
        Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> renewLootChests(), 61);

        Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));
        this.bossTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(IridiumDungeons.getInstance(), () -> checkForBossSpawn(), 0, 2);
        this.breakDungeonTaskId = Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> resetDungeon(), runDuration * 60 * 20).getTaskId();
    }

    public void resetDungeon() {
        if(spawnLocation == null || bossCheckPos1 == null || bossCheckPos2 == null || bossSpawn == null || areaPos1 == null || areaPos2 == null) {
            Bukkit.getLogger().warning("Couldn't reset dungeon, set all positions first and reset the dungeon again.");
            this.isStopped = true;
            return;
        }
        this.isRunning = false;
        this.isStopped = false;
        setTime(LocalDateTime.now());
        cancelTasks();
        EntityDamageByEntityListener.dungeonBossHitters.get(type).clearCooldowns();
        
        Bukkit.getPluginManager().callEvent(new DungeonEndEvent(this));

        if(!players.isEmpty()) {
            getPlayers().forEach(player -> {
                player.teleport(IridiumDungeons.getInstance().getDefaultWorld().getSpawnLocation());
                MessageUtils.sendMessage(player, "Dungeon się zakończył");
            });
            players.clear();
            awaitingPlayers.clear();
        }

        killEntities();

        renewLootChests();
        if(activeBoss != null) {
            Location location = activeBoss.getEntity().getBukkitEntity().getLocation();
            if(!location.getWorld().isChunkLoaded(location.getChunk())) location.getChunk().load();
            activeBoss.despawn();
            activeBoss = null;
        }
    }

    public void stopDungeon() {
        this.isRunning = false;
        this.isStopped = true;
        setTime(LocalDateTime.now());
        cancelTasks();

        getPlayers().forEach(player -> player.teleport(IridiumDungeons.getInstance().getDefaultWorld().getSpawnLocation()));
        players.clear();

        killLootChests();
        activeLootChests.clear();
        killEntities();
    }

    private void cancelTasks() {
        Bukkit.getScheduler().cancelTask(bossTaskId);
        Bukkit.getScheduler().cancelTask(breakDungeonTaskId);
        Bukkit.getScheduler().cancelTask(runDungeonTaskId);
    }

    private void checkForBossSpawn() {
        for(Player player : getPlayers()) {
            BoundingBox bossCheckBox = BoundingBox.of(bossCheckPos1.toVector(), bossCheckPos2.toVector());
            if(bossCheckBox.contains(player.getLocation().toVector())) {
                String bossName = BossType.getFromDungeonType(type).getName();
                Optional<MythicMob> boss = MythicProvider.get().getMobManager().getMythicMob(bossName);
                if(boss.isPresent()) {
                    ActiveMob activeBoss = boss.get().spawn(new AbstractLocation(io.lumine.mythic.bukkit.utils.serialize.Position.of(bossSpawn.toLocation())), 1);
                    activeBoss.getEntity().setCustomName(StringUtils.color("&7" + bossName));
                    activeBoss.getEntity().setMetadata(type.name().toLowerCase() + "DungeonBoss", new FixedMetadataValue(IridiumDungeons.getInstance(), ""));
                    this.activeBoss = activeBoss;
                }
                Bukkit.getScheduler().cancelTask(bossTaskId);
            }
        }
    }

    private void renewLootChests() {
        if(lootChestPositions.size() == 0) return;
        killLootChests();
        activeLootChests.clear();
        Optional<MythicMob> lootChestOptional = IridiumDungeons.getInstance().getDungeonManager().getLootChest();
        if(!lootChestOptional.isPresent()) return;
        loadChestChunks();
        MythicMob lootChest = lootChestOptional.get();
        lootChestPositions.forEach(position -> {
            ActiveMob activeLootChest = lootChest.spawn(LocationUtil.toAbstractLocation(position), 1);
            activeLootChest.getEntity().setCustomName(type.name().toLowerCase() + "dungeon_loot_chest");
            activeLootChests.add(activeLootChest);
        });
        BoundingBox area = BoundingBox.of(areaPos1.toLocation(), areaPos2.toLocation());
        IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld().getEntities().forEach(entity -> {
            if(area.contains(entity.getLocation().toVector())) {
                if(!entity.getName().equals("Loot Chest")) return;
                entity.setCustomName(type.name().toLowerCase() + "dungeon_loot_chest");
            }
        });
        unloadChestChunks();
    }

    public void killLootChests() {
        loadChestChunks();
        activeLootChests.forEach((activeLootChest) -> {
            if(activeLootChest.getEntity() == null) return;
            Location location = activeLootChest.getEntity().getBukkitEntity().getLocation();
            if(!location.getWorld().isChunkLoaded(location.getChunk())) location.getChunk().load();
            activeLootChest.despawn();
        });
        unloadChestChunks();
    }

    public void killEntities() {
        if(areaPos1 == null || areaPos2 == null) return;
        loadChestChunks();
        BoundingBox area = BoundingBox.of(areaPos1.toLocation(), areaPos2.toLocation());
        IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld().getEntities().forEach(entity -> {
            if(area.contains(entity.getLocation().toVector())) {
                if(entity instanceof Player) return;
                entity.remove();
            }
        });
        unloadChestChunks();
    }

    private void loadChestChunks() {
        lootChestPositions.forEach(position -> {
            Location location = position.toLocation();
            if(!location.getWorld().isChunkLoaded(location.getChunk())) location.getChunk().load();
        });
    }

    private void unloadChestChunks() {
        lootChestPositions.forEach(position -> {
            Location location = position.toLocation();
            if(location.getWorld().isChunkLoaded(location.getChunk())) location.getChunk().unload();
        });
    }

    public Location getSpawnLocation() {
        return spawnLocation != null ? spawnLocation.toLocation(IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) : null;
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = new Position(location);
        save();
    }

    public Location getBossCheckPos1() {
        return bossCheckPos1 != null ? bossCheckPos1.toLocation(IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) : null;
    }

    public void setBossCheckPos1(Location location) {
        this.bossCheckPos1 = new Position(location);
        save();
    }

    public Location getBossCheckPos2() {
        return bossCheckPos2 != null ? bossCheckPos2.toLocation(IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) : null;
    }

    public void setBossCheckPos2(Location location) {
        this.bossCheckPos2 = new Position(location);
        save();
    }

    public void setAreaPos1(Location location) {
        this.areaPos1 = new Position(location);
        save();
    }

    public void setAreaPos2(Location location) {
        this.areaPos2 = new Position(location);
        save();
    }

    public Location getBossSpawn() {
        return bossSpawn != null ? bossSpawn.toLocation(IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) : null;
    }

    public void setBossSpawn(Location location) {
        this.bossSpawn = new Position(location);
        save();
    }

    public List<Player> getAwaitingPlayers() {
        return awaitingPlayers.stream()
            .map(uuid -> Bukkit.getPlayer(uuid))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return players.stream()
            .map(uuid -> Bukkit.getPlayer(uuid))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public boolean isInDungeon(Player player) {
        return players.contains(player.getUniqueId());
    }

    public LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public void setTime(LocalDateTime time) {
        this.time = ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public long getRemainingTime() {
        return LocalDateTime.now().until(getTime(), ChronoUnit.SECONDS);
    }

    public boolean isInitialized() {
        return spawnLocation != null;
    }

    public int getBreakDurationInSeconds() {
        return breakDuration * 60;
    }

    public int getRunDurationInSeconds() {
        return runDuration * 60;
    }

    public void setBreakDuration(int breakDuration) {
        this.breakDuration = breakDuration;
        save();
    }
    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
        save();
    }

    public void addLootChest(Location location) {
        if(location.getWorld() != IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) return;
        lootChestPositions.add(new Position(location));
        save();
    }

    public void removeLootChest(Location location) {
        Optional<Position> lootChestPosition = lootChestPositions.stream().sorted((pos1, pos2) -> Double.compare(
            pos1.toLocation().distance(location),
            pos2.toLocation().distance(location)
        )).findFirst();
        if(lootChestPosition.isPresent()) lootChestPositions.remove(lootChestPosition.get());
    }

    // returns false when dungeon's running so you can't join while it's on
    public boolean addAwaitingPlayer(Player player) {
        if(isRunning || isStopped) return false;
        Dungeon playerDungeon = IridiumDungeons.getInstance().getDungeonManager().getRegisteredPlayerDungeon(player);
        if(playerDungeon != null) playerDungeon.removeAwaitingPlayer(player);
        if(awaitingPlayers.isEmpty()) {
            this.runDungeonTaskId = Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> runDungeon(), breakDuration * 60 * 20).getTaskId();
            setTime(LocalDateTime.now().plus(breakDuration, ChronoUnit.MINUTES));
        }
        awaitingPlayers.add(player.getUniqueId());
        for(UUID uuid : awaitingPlayers) {
            Player awaitingPlayer = Bukkit.getPlayer(uuid);
            if(awaitingPlayer != null) MessageUtils.sendMessage(awaitingPlayer, "&aGracz dołączył do dungeona. &7" + awaitingPlayers.size() + "/4");
        }
        if(awaitingPlayers.size() == 4) {
            Bukkit.getScheduler().cancelTask(runDungeonTaskId);
            this.runDungeonTaskId = Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> runDungeon(), 10 * 20).getTaskId();
            for(UUID uuid : awaitingPlayers) {
                Player awaitingPlayer = Bukkit.getPlayer(uuid);
                if(awaitingPlayer != null) MessageUtils.sendMessage(awaitingPlayer, "Dungeon zacznie się za 10 sekund!");
            }
        }
        return true;
    }

    public void removeAwaitingPlayer(Player player) {
        if(awaitingPlayers.contains(player.getUniqueId())) {
            awaitingPlayers.remove(player.getUniqueId());
            if(awaitingPlayers.isEmpty()) {
                Bukkit.getScheduler().cancelTask(runDungeonTaskId);
                runDungeonTaskId = -1;
                setTime(LocalDateTime.now());
            } else {
                for(UUID uuid : awaitingPlayers) {
                    Player awaitingPlayer = Bukkit.getPlayer(uuid);
                    if(awaitingPlayer != null) MessageUtils.sendMessage(awaitingPlayer, "&cGracz odszedł z dungeona. &7" + awaitingPlayers.size() + "/4");
                }
            }
        }
    }

    public void removePlayer(Player player) {
        if(!isRunning) return;
        if(players.contains(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            if(players.isEmpty()) resetDungeon();
        }
    }

    public boolean isRegistered(Player player) {
        return awaitingPlayers.contains(player.getUniqueId());
    }

    public boolean isPlaying(Player player) {
        return players.contains(player.getUniqueId());
    }
    public void save() {
        if(loaded) IridiumDungeons.getInstance().getDatabaseManager().getDatabase().save(this);
    }

    public void toggleShowLoot() {
        if(showLoot) {
            Bukkit.getScheduler().cancelTask(showLootTaskId);
            this.showLootTaskId = -1;
            showLoot = false;
        } else {
            if(this.showLootTaskId == -1) this.showLootTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(IridiumDungeons.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for(ActiveMob lootChest : activeLootChests) {
                        World world = lootChest.getEntity().getBukkitEntity().getWorld();
                        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, lootChest.getEntity().getBukkitEntity().getLocation().add(0, 0.5, 0), 3, 0.1, 0.1, 0.1, new Particle.DustTransition(Color.fromRGB(0, 255, 0), Color.fromRGB(0, 255, 0), 1));
                    }
                }
            }, 0, 2);
            showLoot = true;
        }
    }

    public void addSpawner(Location location, String mobName) {
        if(location.getWorld() != IridiumDungeons.getInstance().getDungeonManager().getDungeonWorld()) return;
        SpawnerManager spawnerManager = MythicBukkit.inst().getSpawnerManager();
        MythicSpawner spawner = spawnerManager.createSpawner(type.name().toLowerCase() + "dungeon_" + String.format("%04d", new Random().nextInt(9999)), location, mobName);
        if(mobName.contains("epic")) {
            spawner.setCooldownSeconds(60);
            spawner.setWarmupSeconds(60);
            spawner.setMaxMobs(PlaceholderInt.of("1"));
        } else {
            spawner.setMaxMobs(PlaceholderInt.of("3"));
            spawner.setCooldownSeconds(10);
            spawner.setWarmupSeconds(10);
        }
        spawner.setGroup(type.name().toLowerCase() + "dungeon");
    }

    public void removeSpawner(Location location) {
        SpawnerManager spawnerManager = MythicBukkit.inst().getSpawnerManager();
        Collection<MythicSpawner> spawners = spawnerManager.getSpawnersByGroup(type.name().toLowerCase() + "dungeon");
        Optional<MythicSpawner> lootChestPosition = spawners.stream().sorted((spawner1, spawner2) -> Double.compare(
            spawner1.distanceTo(LocationUtil.toAbstractLocation(location)),
            spawner2.distanceTo(LocationUtil.toAbstractLocation(location))
        )).findFirst();
        if(lootChestPosition.isPresent()) spawnerManager.removeSpawner(lootChestPosition.get());
    }

    public void broadcastMessage(String message) {
        for(Player player : getPlayers()) {
            MessageUtils.sendMessage(player, message);
        }
    }

    public boolean containsLocation(Location location) {
        BoundingBox area = BoundingBox.of(areaPos1.toLocation(), areaPos2.toLocation());
        return area.contains(location.toVector());
    }

}