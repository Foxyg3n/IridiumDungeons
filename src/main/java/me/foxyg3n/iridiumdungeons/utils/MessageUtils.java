package me.foxyg3n.iridiumdungeons.utils;

import org.bukkit.entity.Player;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;

public class MessageUtils {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(StringUtils.color(("%prefix% &7" + message).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
    }

    public static void sendWarning(Player player, String message) {
        player.sendMessage(StringUtils.color(("%prefix% &c" + message).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
    }

    public static void sendInfo(Player player, String message) {
        player.sendMessage(StringUtils.color(("%prefix% &b" + message).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
    }

}
