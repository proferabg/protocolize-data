package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.MooshroomVariantComponent;
import dev.simplix.protocolize.data.MooshroomVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class MooshroomVariantComponentImpl implements MooshroomVariantComponent {

    MooshroomVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, MooshroomVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<MooshroomVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:mooshroom_variant";
        }

        @Override
        public MooshroomVariantComponent createEmpty() {
            return new MooshroomVariantComponentImpl(null);
        }

        @Override
        public MooshroomVariantComponent create(MooshroomVariant variant) {
            return new MooshroomVariantComponentImpl(variant);
        }
    }

}
