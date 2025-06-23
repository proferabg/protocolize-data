package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.PaintingVariantComponent;
import dev.simplix.protocolize.data.PaintingVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class PaintingVariantComponentImpl implements PaintingVariantComponent {

    PaintingVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, PaintingVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<PaintingVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:painting_variant";
        }

        @Override
        public PaintingVariantComponent createEmpty() {
            return new PaintingVariantComponentImpl(null);
        }

        @Override
        public PaintingVariantComponent create(PaintingVariant variant) {
            return new PaintingVariantComponentImpl(variant);
        }
    }

}
