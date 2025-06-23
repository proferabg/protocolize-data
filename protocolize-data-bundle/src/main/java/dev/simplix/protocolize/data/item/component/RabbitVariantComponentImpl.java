package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.RabbitVariantComponent;
import dev.simplix.protocolize.data.RabbitVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class RabbitVariantComponentImpl implements RabbitVariantComponent {

    RabbitVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, RabbitVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<RabbitVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:rabbit_variant";
        }

        @Override
        public RabbitVariantComponent createEmpty() {
            return new RabbitVariantComponentImpl(null);
        }

        @Override
        public RabbitVariantComponent create(RabbitVariant variant) {
            return new RabbitVariantComponentImpl(variant);
        }
    }

}
