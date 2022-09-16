package me.foxyg3n.iridiumdungeons.skript.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import me.foxyg3n.iridiumdungeons.events.DungeonBeatEvent;

public class DungeonBeatEvt extends SkriptEvent {

    static {
        Skript.registerEvent("Dungeon End", DungeonBeatEvt.class, DungeonBeatEvent.class, "dungeon beat");
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Dungeon Beat event";
    }

    @Override
    public boolean check(Event event) {
        return true;
    }
    
}
