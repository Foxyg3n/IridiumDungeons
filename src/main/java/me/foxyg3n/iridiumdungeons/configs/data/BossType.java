package me.foxyg3n.iridiumdungeons.configs.data;

import me.foxyg3n.iridiumdungeons.DungeonType;

public enum BossType {
    EASY("GiantSkeleton"),
    MEDIUM("FireSorcerer"),
    HARD("ElementalWizard");
    
    private String name;

    private BossType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BossType getFromDungeonType(DungeonType type) {
        return switch(type) {
            case EASY -> BossType.EASY;
            case MEDIUM -> BossType.MEDIUM;
            case HARD -> BossType.HARD;
            default -> BossType.EASY;
        };
    }
}
