package me.foxyg3n.iridiumdungeons.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.foxyg3n.iridiumdungeons.Dungeon;

@Getter
public class DungeonStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Dungeon dungeon;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public DungeonStartEvent(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
