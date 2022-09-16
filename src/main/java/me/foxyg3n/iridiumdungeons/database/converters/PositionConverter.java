package me.foxyg3n.iridiumdungeons.database.converters;

import javax.persistence.AttributeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.foxyg3n.iridiumdungeons.configs.data.Position;

public class PositionConverter implements AttributeConverter<Position, String> {

    private static Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(Position position) {
        if(position == null) return "null";
        JsonObject json = new JsonObject();
        json.addProperty("world", position.getWorld());
        json.addProperty("x", position.getX());
        json.addProperty("y", position.getY());
        json.addProperty("z", position.getZ());
        json.addProperty("pitch", position.getPitch());
        json.addProperty("yaw", position.getYaw());
        return gson.toJson(json);
    }

    @Override
    public Position convertToEntityAttribute(String posString) {
        if(posString.equals("null")) return null;
        JsonObject json = gson.fromJson(posString, JsonObject.class);
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        float pitch = json.get("pitch").getAsFloat();
        float yaw = json.get("yaw").getAsFloat();
        String world = json.get("world").getAsString();
        return new Position(x, y, z, pitch, yaw, world);
    }
    
}