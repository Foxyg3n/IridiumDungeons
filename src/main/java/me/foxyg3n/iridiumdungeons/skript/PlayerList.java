package me.foxyg3n.iridiumdungeons.skript;

import java.util.List;

import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class PlayerList {

    private final List<Player> players;
    
    public PlayerList(List<Player> players) {
        this.players = players;
    }
    
    
}
