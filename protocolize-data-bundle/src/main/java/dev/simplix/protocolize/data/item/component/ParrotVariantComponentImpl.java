package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.ParrotVariantComponent;
import dev.simplix.protocolize.data.ParrotVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class ParrotVariantComponentImpl implements ParrotVariantComponent {

    ParrotVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, ParrotVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<ParrotVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:parrot_variant";
        }

        @Override
        public ParrotVariantComponent createEmpty() {
            return new ParrotVariantComponentImpl(null);
        }

        @Override
        public ParrotVariantComponent create(ParrotVariant variant) {
            return new ParrotVariantComponentImpl(variant);
        }
    }

}
