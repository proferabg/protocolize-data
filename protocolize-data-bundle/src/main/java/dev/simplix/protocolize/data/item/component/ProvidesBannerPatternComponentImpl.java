package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.ProvidesBannerPatternComponent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class ProvidesBannerPatternComponentImpl implements ProvidesBannerPatternComponent {

    String registryKey;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        registryKey = ProtocolUtil.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        ProtocolUtil.writeString(byteBuf, registryKey);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<ProvidesBannerPatternComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:provides_banner_patterns";
        }

        @Override
        public ProvidesBannerPatternComponent createEmpty() {
            return new ProvidesBannerPatternComponentImpl(null);
        }

        @Override
        public ProvidesBannerPatternComponent create(String registryKey) {
            return new ProvidesBannerPatternComponentImpl(registryKey);
        }
    }

}
