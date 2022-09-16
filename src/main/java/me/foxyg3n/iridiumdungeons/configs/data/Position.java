package me.foxyg3n.iridiumdungeons.configs.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonCreator;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Position {
    
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String world;

    @JsonCreator
    public Position(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z, @JsonProperty("pitch") float pitch, @JsonProperty("yaw") float yaw, @JsonProperty("world") String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.world = world;
    }

    public Position(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
        this.world = location.getWorld().getName();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public Location toLocation(World world) {
        this.world = world.getName();
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

}