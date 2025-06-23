package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.TropicalFishPatternComponent;
import dev.simplix.protocolize.data.TropicalFishPattern;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class TropicalFishPatternComponentImpl implements TropicalFishPatternComponent {

    TropicalFishPattern pattern;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        pattern = DataComponentUtil.readRegistry(byteBuf, protocolVersion, TropicalFishPattern.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, pattern);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<TropicalFishPatternComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:tropical_fish_pattern";
        }

        @Override
        public TropicalFishPatternComponent createEmpty() {
            return new TropicalFishPatternComponentImpl(null);
        }

        @Override
        public TropicalFishPatternComponent create(TropicalFishPattern pattern) {
            return new TropicalFishPatternComponentImpl(pattern);
        }
    }

}
