package me.foxyg3n.iridiumdungeons.skript;

import javax.annotation.Nullable;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.foxyg3n.iridiumdungeons.events.DungeonBeatEvent;
import me.foxyg3n.iridiumdungeons.events.DungeonEndEvent;
import me.foxyg3n.iridiumdungeons.events.DungeonStartEvent;

public final class IridiumEventValues {
    
    static {

        EventValues.registerEventValue(DungeonStartEvent.class, PlayerList.class, new Getter<PlayerList, DungeonStartEvent>() {
            @Override
            @Nullable
            public PlayerList get(DungeonStartEvent e) {
                return new PlayerList(e.getDungeon().getPlayers());
            }
        }, 0);

        EventValues.registerEventValue(DungeonEndEvent.class, PlayerList.class, new Getter<PlayerList, DungeonEndEvent>() {
            @Override
            @Nullable
            public PlayerList get(DungeonEndEvent e) {
                return new PlayerList(e.getDungeon().getPlayers());
            }
        }, 0);

        EventValues.registerEventValue(DungeonEndEvent.class, String.class, new Getter<String, DungeonEndEvent>() {
            @Override
            @Nullable
            public String get(DungeonEndEvent e) {
                return e.getDungeon().getType().name().toLowerCase();
            }
        }, 0);

        EventValues.registerEventValue(DungeonBeatEvent.class, PlayerList.class, new Getter<PlayerList, DungeonBeatEvent>() {
            @Override
            @Nullable
            public PlayerList get(DungeonBeatEvent e) {
                return new PlayerList(e.getDungeon().getPlayers());
            }
        }, 0);

        EventValues.registerEventValue(DungeonBeatEvent.class, String.class, new Getter<String, DungeonBeatEvent>() {
            @Override
            @Nullable
            public String get(DungeonBeatEvent e) {
                return e.getDungeon().getType().name().toLowerCase();
            }
        }, 0);

    }

}
