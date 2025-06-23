package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.TropicalFishPatternColorComponent;
import dev.simplix.protocolize.api.item.enums.DyeColor;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class TropicalFishPatternColorComponentImpl implements TropicalFishPatternColorComponent {

    DyeColor color;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        color = DyeColor.values()[ProtocolUtil.readVarInt(byteBuf)];
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        ProtocolUtil.writeVarInt(byteBuf, color.ordinal());
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<TropicalFishPatternColorComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:tropical_fish_pattern_color";
        }

        @Override
        public TropicalFishPatternColorComponent createEmpty() {
            return new TropicalFishPatternColorComponentImpl(null);
        }

        @Override
        public TropicalFishPatternColorComponent create(DyeColor color) {
            return new TropicalFishPatternColorComponentImpl(color);
        }
    }

}
