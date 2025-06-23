package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CatVariantComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.CatVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class CatVariantComponentImpl implements CatVariantComponent {

    CatVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, CatVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<CatVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:cat_variant";
        }

        @Override
        public CatVariantComponent createEmpty() {
            return new CatVariantComponentImpl(null);
        }

        @Override
        public CatVariantComponent create(CatVariant variant) {
            return new CatVariantComponentImpl(variant);
        }
    }

}
