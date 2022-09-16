package me.foxyg3n.iridiumdungeons.database;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MythicMob;
import me.foxyg3n.iridiumdungeons.Dungeon;
import me.foxyg3n.iridiumdungeons.DungeonType;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class DungeonManager {

    public static final String DUNGEON_WORLD_NAME = IridiumSkyblock.getInstance().getConfiguration().worldName + "_dungeons";

    public World dungeonWorld; // TODO: Set gamerules for the world (disable commands, vine spreading, etc.)
    public Dungeon easyDungeon;
    public Dungeon mediumDungeon;
    public Dungeon hardDungeon;

    private List<Dungeon> dungeonDefaultList = Arrays.asList(

        new Dungeon(0, DungeonType.EASY, new Item(XMaterial.ZOMBIE_HEAD, 10, 1, DungeonType.EASY.getName(), Arrays.asList(
            "&7Czy czujesz się źle ze swoim życiem? Zapisz się na ten dungeon",
            "&7i zdobądź bogactwa o jakich nie śniłeś.",
            " ",
            "&b&lInformacje:",
            "&b&l * &7Stan: &b%dungeon_state%",
            "&b&l * &7Gracze: &b%dungeon_playercount%/4",
            "&b&l * &7Pozostały Czas: &b%timer_minutes%m %timer_seconds%s",
            "&b&l * &7Koszt Dungeona: &b$%vaultcost% monet %hasfreepass%",
            "&b&l * &7Sugerowany Set: &bŻelazny Set - Ochrona II",
            "&b&l * &7Sugerowana Broń: &bŻelazny Miecz - Ostrość II",
            " ",
            "&b&l[!] &bKliknij, aby zapisać się do tego dungeona."
        )), 3000, 5, 15),

        new Dungeon(1, DungeonType.MEDIUM, new Item(XMaterial.SKELETON_SKULL, 12, 1, DungeonType.MEDIUM.getName(), Arrays.asList(
            "&7Czy czujesz się źle ze swoim życiem? Zapisz się na ten dungeon",
            "&7i zdobądź bogactwa o jakich nie śniłeś.",
            " ",
            "&b&lInformacje:",
            "&b&l * &7Stan: &b%dungeon_state%",
            "&b&l * &7Gracze: &b%dungeon_playercount%/4",
            "&b&l * &7Pozostały Czas: &b%timer_minutes%m %timer_seconds%s",
            "&b&l * &7Koszt Dungeona: &b$%vaultcost% monet %hasfreepass%",
            "&b&l * &7Sugerowany Set: &bDiamentowy Set - Ochrona II",
            "&b&l * &7Sugerowana Broń: &bDiamentowy Miecz - Ostrość III",
            " ",
            "&b&l[!] &bKliknij, aby zapisać się do tego dungeona."
        )), 8000, 5, 20),
        
        new Dungeon(2, DungeonType.HARD, new Item(XMaterial.WITHER_SKELETON_SKULL, 14, 1, DungeonType.HARD.getName(), Arrays.asList(
            "&7Czy czujesz się źle ze swoim życiem? Zapisz się na ten dungeon",
            "&7i zdobądź bogactwa o jakich nie śniłeś.",
            " ",
            "&b&lInformacje:",
            "&b&l * &7Stan: &b%dungeon_state%",
            "&b&l * &7Gracze: &b%dungeon_playercount%/4",
            "&b&l * &7Pozostały Czas: &b%timer_minutes%m %timer_seconds%s",
            "&b&l * &7Koszt Dungeona: &b$%vaultcost% monet %hasfreepass%",
            "&b&l * &7Sugerowany Set: &bDiamentowy Set - Ochrona IV",
            "&b&l * &7Sugerowana Broń: &bDiamentowy Miecz - Ostrość V",
            " ",
            "&b&l[!] &bKliknij, aby zapisać się do tego dungeona."
        )), 12500, 5, 30)

    );

    public DungeonManager() {
        loadDungeons();
        saveAll();
    }

    public void loadDungeons() {
        Database database = IridiumDungeons.getInstance().getDatabaseManager().getDatabase();
        this.easyDungeon = database.contains(Dungeon.class, 0) ? database.get(Dungeon.class, 0) : dungeonDefaultList.get(0);
        this.mediumDungeon = database.contains(Dungeon.class, 1) ? database.get(Dungeon.class, 1) : dungeonDefaultList.get(1);
        this.hardDungeon = database.contains(Dungeon.class, 2) ? database.get(Dungeon.class, 2) : dungeonDefaultList.get(2);
    }

    public Dungeon getDungeonById(int id) {
        return getDungeonList().stream().filter(dungeon -> dungeon.getId() == id).findFirst().orElse(null);
    }

    public Dungeon getRegisteredPlayerDungeon(Player player) {
        return easyDungeon.isRegistered(player) ? easyDungeon :
            mediumDungeon.isRegistered(player) ? mediumDungeon :
            hardDungeon.isRegistered(player) ? hardDungeon : null;
    }

    public Dungeon getPlayingPlayerDungeon(Player player) {
        return easyDungeon.isPlaying(player) ? easyDungeon :
            mediumDungeon.isPlaying(player) ? mediumDungeon :
            hardDungeon.isPlaying(player) ? hardDungeon : null;
    }

    public static void loadDungeonWorld() {
        World world = Bukkit.createWorld(new WorldCreator(DUNGEON_WORLD_NAME).environment(World.Environment.NORMAL));

        if(Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
            Bukkit.getScheduler().runTaskLater(IridiumDungeons.getInstance(), () -> {
                String worldGenerator = Bukkit.getPluginManager().isPluginEnabled("VoidGen") ? "VoidGen" : IridiumSkyblock.getInstance().getName();
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + world.getName() + " normal -g " + worldGenerator);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + worldGenerator + " " + world.getName());
            }, 1);
        }
    }

    public World getDungeonWorld() {
        return dungeonWorld != null ? dungeonWorld : Bukkit.getWorld(DUNGEON_WORLD_NAME);
    }

    public List<Dungeon> getDungeonList() {
        return Arrays.asList(easyDungeon, mediumDungeon, hardDungeon);
    }

    public void saveAll() {
        getDungeonList().forEach(Dungeon::save);
    }

    public Optional<MythicMob> getBoss(String dungeonType) {
        return switch(dungeonType) {
            case "easy dungeon" -> MythicProvider.get().getMobManager().getMythicMob("SkeletonDS");
            case "medium dungeon" -> MythicProvider.get().getMobManager().getMythicMob("");
            case "hard dungeon" -> MythicProvider.get().getMobManager().getMythicMob("");
            default -> Optional.empty();
        };
    }

    public Optional<MythicMob> getLootChest() {
        return MythicProvider.get().getMobManager().getMythicMob("loot_chest");
    }

}
