package me.foxyg3n.iridiumdungeons.database.converters;

import java.io.IOException;
import java.util.List;

import com.iridium.iridiumcore.dependencies.fasterxml.core.JsonGenerator;
import com.iridium.iridiumcore.dependencies.fasterxml.databind.SerializerProvider;
import com.iridium.iridiumcore.dependencies.fasterxml.databind.ser.std.StdSerializer;
import com.iridium.iridiumcore.utils.ItemStackUtils;

import org.bukkit.inventory.ItemStack;

public class ItemStackListSerializer extends StdSerializer<List<ItemStack>> {

    public ItemStackListSerializer() { 
        this(null); 
     } 

    public ItemStackListSerializer(Class<List<ItemStack>> t) { 
        super(t); 
    } 

    @Override
    public void serialize(List<ItemStack> itemStackList, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for(ItemStack itemStack : itemStackList) {
            gen.writeRawValue(ItemStackUtils.serialize(itemStack));
        }
        gen.writeEndArray();
    }
    
}