package me.foxyg3n.iridiumdungeons.configs;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class ResourcePack {

    private File resourcePackConfigFile;
    private FileConfiguration resourcePackConfig;

    public ResourcePack init() {
        this.resourcePackConfig = loadResourcePackConfig();
        resourcePackConfig.addDefault("resourcepack", "insert-url");

        setResourcePack(resourcePackConfig.getString("resourcepack"));

        resourcePackConfig.options().copyDefaults(true);
        save();
        return this;
    }
    
    public String resourcePackUrl = null;

    public FileConfiguration loadResourcePackConfig() {
        JavaPlugin plugin = IridiumDungeons.getInstance();
        resourcePackConfigFile = new File(plugin.getDataFolder(), "resourcepack.yml");
        if (!resourcePackConfigFile.exists()) {
            resourcePackConfigFile.getParentFile().mkdirs();
            try {
                resourcePackConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration resourcePackConfig = new YamlConfiguration();
        try {
            resourcePackConfig.load(resourcePackConfigFile);
            return resourcePackConfig;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save() {
        try {
            resourcePackConfig.set("resourcepack", resourcePackUrl);
            this.resourcePackConfig.save(resourcePackConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setResourcePack(String url) {
        resourcePackUrl = url;
        save();
    }

}
