package me.foxyg3n.iridiumdungeons.skript.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import me.foxyg3n.iridiumdungeons.events.DungeonEndEvent;

public class DungeonEndEvt extends SkriptEvent {

    static {
        Skript.registerEvent("Dungeon End", DungeonEndEvt.class, DungeonEndEvent.class, "dungeon end");
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Dungeon End event";
    }

    @Override
    public boolean check(Event event) {
        return true;
    }
    
}
