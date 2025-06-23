package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.VillagerVariantComponent;
import dev.simplix.protocolize.data.VillagerType;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class VillagerVariantComponentImpl implements VillagerVariantComponent {

    VillagerType villagerType;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        villagerType = DataComponentUtil.readRegistry(byteBuf, protocolVersion, VillagerType.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, villagerType);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<VillagerVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:villager_variant";
        }

        @Override
        public VillagerVariantComponent createEmpty() {
            return new VillagerVariantComponentImpl(null);
        }

        @Override
        public VillagerVariantComponent create(VillagerType type) {
            return new VillagerVariantComponentImpl(type);
        }
    }

}
