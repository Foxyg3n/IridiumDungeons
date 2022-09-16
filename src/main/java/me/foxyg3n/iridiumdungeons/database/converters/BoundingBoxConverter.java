package me.foxyg3n.iridiumdungeons.database.converters;

import javax.persistence.AttributeConverter;

import org.bukkit.util.BoundingBox;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class BoundingBoxConverter implements AttributeConverter<BoundingBox, String> {

    private static Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(BoundingBox boundingBox) {
        if(boundingBox == null) return "null";
        JsonObject json = new JsonObject();
        json.addProperty("minX", boundingBox.getMinX());
        json.addProperty("minY", boundingBox.getMinY());
        json.addProperty("minZ", boundingBox.getMinZ());
        json.addProperty("maxX", boundingBox.getMaxX());
        json.addProperty("maxY", boundingBox.getMaxY());
        json.addProperty("maxZ", boundingBox.getMaxZ());
        return gson.toJson(json);
    }

    @Override
    public BoundingBox convertToEntityAttribute(String boundingBoxString) {
        if(boundingBoxString.equals("null")) return null;
        JsonObject json = gson.fromJson(boundingBoxString, JsonObject.class);
        double minX = json.get("minX").getAsDouble();
        double minY = json.get("minY").getAsDouble();
        double minZ = json.get("minZ").getAsDouble();
        double maxX = json.get("maxX").getAsDouble();
        double maxY = json.get("maxY").getAsDouble();
        double maxZ = json.get("maxZ").getAsDouble();
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
}