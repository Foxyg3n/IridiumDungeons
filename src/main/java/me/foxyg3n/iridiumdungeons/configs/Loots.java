package me.foxyg3n.iridiumdungeons.configs;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.core.drops.DropTable;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class Loots {

    private File lootConfigFile;
    private FileConfiguration lootConfig;

    public Loots init() {
        this.lootConfig = loadLootConfig();
        lootConfig.addDefault("EasyDungeonLoot", "EasyDungeonLoot");
        lootConfig.addDefault("MediumDungeonLoot", "MediumDungeonLoot");
        lootConfig.addDefault("HardDungeonLoot", "HardDungeonLoot");
        lootConfig.addDefault("EasyDungeonBossLoot", "EasyDungeonBossLoot");
        lootConfig.addDefault("MediumDungeonBossLoot", "MediumDungeonBossLoot");
        lootConfig.addDefault("HardDungeonBossLoot", "HardDungeonBossLoot");

        this.easyDungeonLoot = getDropTable("EasyDungeonLoot");
        this.mediumDungeonLoot = getDropTable("MediumDungeonLoot");
        this.hardDungeonLoot = getDropTable("HardDungeonLoot");
        this.easyDungeonBossLoot = getDropTable("EasyDungeonBossLoot");
        this.mediumDungeonBossLoot = getDropTable("MediumDungeonBossLoot");
        this.hardDungeonBossLoot = getDropTable("HardDungeonBossLoot");

        lootConfig.options().copyDefaults(true);
        try {
            lootConfig.save(lootConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    public Optional<DropTable> easyDungeonLoot;
    public Optional<DropTable> mediumDungeonLoot;
    public Optional<DropTable> hardDungeonLoot;
    
    public Optional<DropTable> easyDungeonBossLoot;
    public Optional<DropTable> mediumDungeonBossLoot;
    public Optional<DropTable> hardDungeonBossLoot;

    public FileConfiguration loadLootConfig() {
        JavaPlugin plugin = IridiumDungeons.getInstance();
        lootConfigFile = new File(plugin.getDataFolder(), "loots.yml");
        if (!lootConfigFile.exists()) {
            lootConfigFile.getParentFile().mkdirs();
            try {
                lootConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration lootConfig = new YamlConfiguration();
        try {
            lootConfig.load(lootConfigFile);
            return lootConfig;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Optional<DropTable> getDropTable(String node) {
        return MythicProvider.get().getDropManager().getDropTable(lootConfig.getString(node));
    }

}
