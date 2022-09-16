package me.foxyg3n.iridiumdungeons.utils;

import org.bukkit.Location;

import io.lumine.mythic.api.adapters.AbstractLocation;
import me.foxyg3n.iridiumdungeons.configs.data.Position;

public class LocationUtil {
    
    public static AbstractLocation toAbstractLocation(Position position) {
        return toAbstractLocation(position.toLocation());
    }
    
    public static AbstractLocation toAbstractLocation(Location location) {
        AbstractLocation targetLocation = new AbstractLocation(io.lumine.mythic.bukkit.utils.serialize.Position.of(location));
        targetLocation.setPitch(location.getPitch());
        targetLocation.setYaw(location.getYaw());
        return targetLocation;
    }

}
