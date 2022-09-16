package me.foxyg3n.iridiumdungeons.database.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.AttributeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.foxyg3n.iridiumdungeons.configs.data.Position;

public class PositionListConverter implements AttributeConverter<List<Position>, String> {

    private static Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List<Position> positionList) {
        if(positionList == null) return "null";
        JsonArray positionArrayJson = new JsonArray();
        for(Position position : positionList) {
            JsonObject positionJson = new JsonObject();
            positionJson.addProperty("world", position.getWorld());
            positionJson.addProperty("x", position.getX());
            positionJson.addProperty("y", position.getY()); 
            positionJson.addProperty("z", position.getZ());
            positionJson.addProperty("pitch", position.getPitch());
            positionJson.addProperty("yaw", position.getYaw());
            positionArrayJson.add(positionJson);
        }
        return gson.toJson(positionArrayJson);
    }

    @Override
    public List<Position> convertToEntityAttribute(String posListString) {
        if(posListString.equals("null")) return null;
        JsonArray positionArrayJson = gson.fromJson(posListString, JsonArray.class);
        Iterator<JsonElement> iterator = positionArrayJson.iterator();
        List<Position> positionList = new ArrayList<Position>();
        while(iterator.hasNext()) {
            JsonObject positionJson = iterator.next().getAsJsonObject();
            double x = positionJson.get("x").getAsDouble();
            double y = positionJson.get("y").getAsDouble();
            double z = positionJson.get("z").getAsDouble();
            float pitch = positionJson.get("pitch").getAsFloat();
            float yaw = positionJson.get("yaw").getAsFloat();
            String world = positionJson.get("world").getAsString();
            Position position = new Position(x, y, z, pitch, yaw, world);
            positionList.add(position);
        }
        return positionList;
    }
    
}