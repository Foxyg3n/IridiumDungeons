package me.foxyg3n.iridiumdungeons.skript.expressions;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.foxyg3n.iridiumdungeons.skript.PlayerList;

public class PlayerExpr extends SimpleExpression<Player> {

    static {
        Skript.registerExpression(PlayerExpr.class, Player.class, ExpressionType.SIMPLE, "[the] players of %playerlist%");
    }
    Expression<PlayerList> playerListExpression;

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        playerListExpression = (Expression<PlayerList>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return  "the players of " + playerListExpression.toString(event, debug);
    }

    @Override
    @Nullable
    protected Player[] get(Event event) {
        PlayerList playerList = playerListExpression.getSingle(event);
        if(playerList == null) return new Player[] { null };

        return playerList.getPlayers().toArray(new Player[0]);
    }
    
}