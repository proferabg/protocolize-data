package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.AxolotlVariantComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.AxolotlVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class AxolotlVariantComponentImpl implements AxolotlVariantComponent {

    AxolotlVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, AxolotlVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<AxolotlVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:axolotl_variant";
        }

        @Override
        public AxolotlVariantComponent createEmpty() {
            return new AxolotlVariantComponentImpl(null);
        }

        @Override
        public AxolotlVariantComponent create(AxolotlVariant variant) {
            return new AxolotlVariantComponentImpl(variant);
        }
    }

}
