package me.foxyg3n.iridiumdungeons.configs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.foxyg3n.iridiumdungeons.DungeonType;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class FreePasses {
    
    private File freePassesConfigFile;
    private FileConfiguration freePassesConfig;
    
    private List<UUID> freeEasyPasses = new ArrayList<>();
    private List<UUID> freeMediumPasses = new ArrayList<>();
    private List<UUID> freeHardPasses = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public FreePasses init() {
        this.freePassesConfig = loadFreePassesConfig();
        freePassesConfig.addDefault("easyPasses", new ArrayList<>());
        freePassesConfig.addDefault("mediumPasses", new ArrayList<>());
        freePassesConfig.addDefault("hardPasses", new ArrayList<>());

        freeEasyPasses = convertToUUIDList((List<String>) freePassesConfig.getList("easyPasses"));
        freeMediumPasses = convertToUUIDList((List<String>) freePassesConfig.getList("mediumPasses"));
        freeHardPasses = convertToUUIDList((List<String>) freePassesConfig.getList("hardPasses"));

        freePassesConfig.options().copyDefaults(true);
        save();
        return this;
    }

    public FileConfiguration loadFreePassesConfig() {
        JavaPlugin plugin = IridiumDungeons.getInstance();
        freePassesConfigFile = new File(plugin.getDataFolder(), "freepasses.yml");
        if (!freePassesConfigFile.exists()) {
            freePassesConfigFile.getParentFile().mkdirs();
            try {
                freePassesConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration freePassesConfig = new YamlConfiguration();
        try {
            freePassesConfig.load(freePassesConfigFile);
            return freePassesConfig;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<DungeonType, List<UUID>> getPassesHashMap() {
        HashMap<DungeonType, List<UUID>> passes = new HashMap<>();
        passes.put(DungeonType.EASY, freeEasyPasses);
        passes.put(DungeonType.MEDIUM, freeMediumPasses);
        passes.put(DungeonType.HARD, freeHardPasses);
        return passes;
    }

    private List<UUID> convertToUUIDList(List<String> list) {
        return list.stream().map(UUID::fromString).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private List<String> convertToStringList(List<UUID> list) {
        return list.stream().map(UUID::toString).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void save() {
        try {
            freePassesConfig.set("easyPasses", convertToStringList(freeEasyPasses));
            freePassesConfig.set("mediumPasses", convertToStringList(freeMediumPasses));
            freePassesConfig.set("hardPasses", convertToStringList(freeHardPasses));
            this.freePassesConfig.save(freePassesConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableFreePass(Player player, DungeonType type) {
        List<UUID> freePasses = getPassesHashMap().get(type);
        if(freePasses.contains(player.getUniqueId())) return;
        freePasses.add(player.getUniqueId());
        save();
    }

    public void disableFreePass(Player player, DungeonType type) {
        List<UUID> freePasses = getPassesHashMap().get(type);
        if(!freePasses.contains(player.getUniqueId())) return;
        freePasses.remove(player.getUniqueId());
        save();
    }

    public boolean hasFreePass(Player player, DungeonType type) {
        return getPassesHashMap().get(type).contains(player.getUniqueId());
    }

}
