package me.foxyg3n.iridiumdungeons;

import lombok.Getter;

@Getter
public enum DungeonType {
    EASY("Łatwy Dungeon"),
    MEDIUM("Średni Dungeon"),
    HARD("Trudny Dungeon");

    private String name;

    DungeonType(String name) {
        this.name = name;
    }
}
