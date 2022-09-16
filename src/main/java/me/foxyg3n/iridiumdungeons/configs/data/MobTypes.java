package me.foxyg3n.iridiumdungeons.configs.data;

import java.util.List;

import lombok.Getter;
import me.foxyg3n.iridiumdungeons.DungeonType;

@Getter
public class MobTypes {

    private DungeonType dungeonType;
    private List<String> mobs;
    
    public MobTypes(DungeonType dungeonType, List<String> mobs) {
        this.dungeonType = dungeonType;
        this.mobs = mobs;
    }

    // public String fromMobType(DungeonMobType mobType) {
    //     return switch(mobType) {
    //         case BASIC1 -> basic1;
    //         case BASIC2 -> basic2;
    //         case ELITE -> elite;
    //         case BOSS -> boss;
    //     };
    // }

}
