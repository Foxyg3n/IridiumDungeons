package me.foxyg3n.iridiumdungeons;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.iridium.iridiumcore.IridiumCore;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import lombok.Getter;
import me.foxyg3n.iridiumdungeons.commands.DungeonCommand;
import me.foxyg3n.iridiumdungeons.commands.ResourcePackCommand;
import me.foxyg3n.iridiumdungeons.commands.TestCommand;
import me.foxyg3n.iridiumdungeons.configs.FreePasses;
import me.foxyg3n.iridiumdungeons.configs.Inventories;
import me.foxyg3n.iridiumdungeons.configs.Loots;
import me.foxyg3n.iridiumdungeons.configs.ResourcePack;
import me.foxyg3n.iridiumdungeons.database.DatabaseManager;
import me.foxyg3n.iridiumdungeons.database.DungeonManager;
import me.foxyg3n.iridiumdungeons.listeners.EntityDamageByEntityListener;
import me.foxyg3n.iridiumdungeons.listeners.IridiumSkyblockReloadListener;
import me.foxyg3n.iridiumdungeons.listeners.MythicMobDeathListener;
import me.foxyg3n.iridiumdungeons.listeners.MythicMobsReloadListener;
import me.foxyg3n.iridiumdungeons.listeners.PlayerDeathListener;
import me.foxyg3n.iridiumdungeons.listeners.PlayerJoinListener;
import me.foxyg3n.iridiumdungeons.listeners.PlayerLeaveListener;
import me.foxyg3n.iridiumdungeons.listeners.PlayerRespawnListener;
import me.foxyg3n.iridiumdungeons.listeners.RightClickListener;
import me.foxyg3n.iridiumdungeons.skript.IridiumEventValues;

@Getter
public class IridiumDungeons extends IridiumCore {

    private static IridiumDungeons instance;
    private SkriptAddon skriptAddon;

    private DatabaseManager databaseManager;
    private DungeonManager dungeonManager;

    private Inventories inventories;
    private Loots loots;
    private ResourcePack resourcePack;
    private FreePasses freePasses;

    public IridiumDungeons() {
        instance = this;
    }

    public static IridiumDungeons getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        super.onLoad();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();

        loadCustomConfigs();
        DungeonManager.loadDungeonWorld();

        this.databaseManager = new DatabaseManager();
        databaseManager.registerDatabaseConnection();
        this.dungeonManager = new DungeonManager();

        registerSkript();

        getCommand("setresourcepack").setExecutor(new ResourcePackCommand());
        
        IridiumSkyblockAPI.getInstance().addCommand(new DungeonCommand());
        IridiumSkyblockAPI.getInstance().addCommand(new TestCommand());

        Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new MythicMobDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new MythicMobsReloadListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new IridiumSkyblockReloadListener(), this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> dungeonManager.getDungeonList().forEach(Dungeon::resetDungeon));

        getLogger().info("Iridium Dungeons expansion has been enabled!");
    }

    @Override
    public void onDisable() {
        dungeonManager.getDungeonList().forEach(Dungeon::killLootChests);
        getLogger().info("Iridium Dungeons expansion has been disabled!");
    }

    @Override
    public void saveData() {
        getDungeonManager().saveAll();
        this.freePasses.save();
    }

    @Override
    public void loadConfigs() {
        this.inventories = getPersist().load(Inventories.class);
        this.freePasses = new FreePasses().init();
    }

    public void loadCustomConfigs() {
        this.loots = new Loots().init();
        this.resourcePack = new ResourcePack().init();
    }
    
    @Override
    public void saveConfigs() {
        getPersist().save(inventories);
        this.freePasses.save();
    }

    public World getDefaultWorld() {
        return getServer().getWorlds().get(0);
    }
    
    private void registerSkript() {
        if(getServer().getPluginManager().getPlugin("Skript") != null) {
            getLogger().info("Skript was found! Hooking into Skript");
            skriptAddon = Skript.registerAddon(this);
            try {
                skriptAddon.loadClasses("me.foxyg3n.iridiumdungeons.skript", "events", "classes", "expressions", "effects");
                new IridiumEventValues();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getLogger().info("Skript addon registered!");
        } else {
            getLogger().warning("Could not find Skript! Ommiting hooking.");
        }
    }

}