package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.SalmonSizeComponent;
import dev.simplix.protocolize.data.SalmonVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class SalmonSizeComponentImpl implements SalmonSizeComponent {

    SalmonVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, SalmonVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<SalmonSizeComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:salmon_size";
        }

        @Override
        public SalmonSizeComponent createEmpty() {
            return new SalmonSizeComponentImpl(null);
        }

        @Override
        public SalmonSizeComponent create(SalmonVariant variant) {
            return new SalmonSizeComponentImpl(variant);
        }
    }

}
