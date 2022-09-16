package me.foxyg3n.iridiumdungeons.skript.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import me.foxyg3n.iridiumdungeons.events.DungeonStartEvent;

public class DungeonStartEvt extends SkriptEvent {

    static {
        Skript.registerEvent("Dungeon Start", DungeonStartEvt.class, DungeonStartEvent.class, "dungeon start");
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Dungeon Start event";
    }

    @Override
    public boolean check(Event event) {
        return true;
    }
    
}
