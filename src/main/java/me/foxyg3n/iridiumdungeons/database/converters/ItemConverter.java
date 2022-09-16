package me.foxyg3n.iridiumdungeons.database.converters;

import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;

import org.apache.commons.lang.StringUtils;

public class ItemConverter implements AttributeConverter<Item, String> {

    private static Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(Item item) {
        if(item == null) return "null";
        JsonObject json = new JsonObject();
        json.addProperty("xmaterial", item.material.name());
        json.addProperty("slot", item.slot);
        json.addProperty("amount", item.amount);
        json.addProperty("name", item.displayName);
        json.addProperty("lore", StringUtils.join(item.lore, "`"));
        return gson.toJson(json);
    }

    @Override
    public Item convertToEntityAttribute(String itemString) {
        if(itemString.equals("null")) return null;
        JsonObject json = gson.fromJson(itemString, JsonObject.class);
        XMaterial material = XMaterial.matchXMaterial(json.get("xmaterial").getAsString()).get();
        int slot = json.get("slot").getAsInt();
        int amount = json.get("amount").getAsInt();
        String name = json.get("name").getAsString();
        List<String> lore = Arrays.asList(StringUtils.split(json.get("lore").getAsString(), "`"));
        return new Item(material, slot, amount, name, lore);
    }
    
}