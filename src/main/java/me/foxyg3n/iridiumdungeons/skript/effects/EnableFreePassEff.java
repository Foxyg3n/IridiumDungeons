package me.foxyg3n.iridiumdungeons.skript.effects;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.foxyg3n.iridiumdungeons.DungeonType;
import me.foxyg3n.iridiumdungeons.IridiumDungeons;

public class EnableFreePassEff extends Effect {

    static {
        Skript.registerEffect(EnableFreePassEff.class,
        "enable (free|dungeon) pass for %player% on easy dungeon",
        "enable (free|dungeon) pass for %player% on medium dungeon",
        "enable (free|dungeon) pass for %player% on hard dungeon");
    }
    private Expression<Player> playerExpression;
    int option;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        playerExpression = (Expression<Player>) exprs[0];
        option = matchedPattern;
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "enable dungeon pass for player " + playerExpression.toString(event, debug);
    }

    @Override
    protected void execute(Event event) {
        DungeonType type = switch(option) {
            case 0 -> DungeonType.EASY;
            case 1 -> DungeonType.MEDIUM;
            case 2 -> DungeonType.HARD;
            default -> DungeonType.EASY;
        };
        Player player = playerExpression.getSingle(event);
        IridiumDungeons.getInstance().getFreePasses().enableFreePass(player, type);
    }
    
}
