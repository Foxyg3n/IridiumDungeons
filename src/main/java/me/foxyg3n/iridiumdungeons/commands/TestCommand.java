package me.foxyg3n.iridiumdungeons.commands;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.iridium.iridiumskyblock.commands.Command;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.utils.LocationUtil;

public class TestCommand extends Command {

    public TestCommand() {
        super(Collections.singletonList("test"), "Test command", "%prefix% &7/is test", "*", false, Duration.ZERO);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Optional<MythicMob> optional = IridiumDungeons.getInstance().getDungeonManager().getLootChest();
            if(optional.isPresent()) {
                AbstractLocation location = LocationUtil.toAbstractLocation(player.getLocation());
                MythicMob lootChest = optional.get();
                ActiveMob activeLootChest = lootChest.spawn(location.add(0, 1, 0), 1);
                activeLootChest.getEntity().getBukkitEntity().teleport(player.getLocation());
                sender.sendMessage("Spawned lootChest");
            } else {
                sender.sendMessage("No lootChest");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
    
}
