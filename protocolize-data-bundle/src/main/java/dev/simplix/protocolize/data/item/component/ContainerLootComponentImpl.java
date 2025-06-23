package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.ContainerLootComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.StringTag;

import java.io.IOException;

@Data
@AllArgsConstructor
public class ContainerLootComponentImpl implements ContainerLootComponent {

    private String lootTable;
    private Long seed;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException{
        CompoundTag data = (CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
        if(data != null){
            if(data.containsKey("loot_table"))
                lootTable = data.getStringTag("loot_table").getValue();
            if(data.containsKey("seed"))
                seed = data.getLongTag("seed").asLong();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        CompoundTag data = new CompoundTag();
        if(lootTable != null){
            data.put("loot_table", new StringTag(lootTable));
        }
        if(seed != null){
            data.put("seed", new LongTag(seed));
        }
        NamedBinaryTagUtil.writeTag(byteBuf, data, protocolVersion);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<ContainerLootComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:container_loot";
        }

        @Override
        public ContainerLootComponent createEmpty() {
            return new ContainerLootComponentImpl(null, null);
        }

        @Override
        public ContainerLootComponent create(String lootTable) {
            return new ContainerLootComponentImpl(lootTable, null);
        }

        @Override
        public ContainerLootComponent create(String lootTable, Long seed) {
            return new ContainerLootComponentImpl(lootTable, seed);
        }
    }

}
