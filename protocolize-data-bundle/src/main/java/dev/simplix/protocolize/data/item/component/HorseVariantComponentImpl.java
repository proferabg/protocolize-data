package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.HorseVariantComponent;
import dev.simplix.protocolize.data.HorseVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class HorseVariantComponentImpl implements HorseVariantComponent {

    HorseVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, HorseVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<HorseVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:horse_variant";
        }

        @Override
        public HorseVariantComponent createEmpty() {
            return new HorseVariantComponentImpl(null);
        }

        @Override
        public HorseVariantComponent create(HorseVariant variant) {
            return new HorseVariantComponentImpl(variant);
        }
    }

}
