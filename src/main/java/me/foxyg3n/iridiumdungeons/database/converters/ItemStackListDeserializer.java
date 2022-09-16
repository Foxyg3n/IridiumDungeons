package me.foxyg3n.iridiumdungeons.database.converters;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.iridium.iridiumcore.dependencies.fasterxml.core.JacksonException;
import com.iridium.iridiumcore.dependencies.fasterxml.core.JsonParser;
import com.iridium.iridiumcore.dependencies.fasterxml.databind.DeserializationContext;
import com.iridium.iridiumcore.dependencies.fasterxml.databind.deser.std.StdDeserializer;
import com.iridium.iridiumcore.utils.ItemStackUtils;

import org.bukkit.inventory.ItemStack;

public class ItemStackListDeserializer extends StdDeserializer<List<ItemStack>> {

    public ItemStackListDeserializer() { 
        this(null); 
     } 

    public ItemStackListDeserializer(Class<List<ItemStack>> t) { 
        super(t); 
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ItemStack> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        List<String> itemStackList = (List<String>) parser.readValueAs(List.class);
        return itemStackList.stream().map(itemStack -> ItemStackUtils.deserialize(itemStack)).collect(Collectors.toList());
    }
    
}