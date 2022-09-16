package me.foxyg3n.iridiumdungeons.skript.classes;

import java.util.List;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import me.foxyg3n.iridiumdungeons.skript.PlayerList;

public class PotionListClassInfo {
    
    static {
        Classes.registerClass(new ClassInfo<>(PlayerList.class, "playerlist")
            .user("playerlist")
            .name("Playerlist")
            .description("represents a list of players")
            .defaultExpression(new EventValueExpression<>(PlayerList.class))
            .parser(new Parser<PlayerList>() {

                @Override
                @Nullable
                public PlayerList parse(String s, ParseContext context) {
                    return null;
                }

                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String getVariableNamePattern() {
                    return null;
                }

                @Override
                public String toVariableNameString(PlayerList playerList) {
                    List<Player> players = playerList.getPlayers();
                    StringBuilder sb = new StringBuilder();
                    for(Player player : players) {
                        sb.append(player.getName());
                        sb.append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    return sb.toString();
                }

                @Override
                public String toString(PlayerList island, int flags) {
                    return toVariableNameString(island);
                }
                
            })
        );
    }

}
