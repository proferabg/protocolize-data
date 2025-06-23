package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CowVariantComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.CowVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class CowVariantComponentImpl implements CowVariantComponent {

    CowVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, CowVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<CowVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:cow_variant";
        }

        @Override
        public CowVariantComponent createEmpty() {
            return new CowVariantComponentImpl(null);
        }

        @Override
        public CowVariantComponent create(CowVariant variant) {
            return new CowVariantComponentImpl(variant);
        }
    }

}
