package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.SheepColorComponent;
import dev.simplix.protocolize.api.item.enums.DyeColor;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class SheepColorComponentImpl implements SheepColorComponent {

    private DyeColor color;

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

    public static class Type implements DataComponentType<SheepColorComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:sheep_color";
        }

        @Override
        public SheepColorComponent createEmpty() {
            return new SheepColorComponentImpl(null);
        }

        @Override
        public SheepColorComponent create(DyeColor color) {
            return new SheepColorComponentImpl(color);
        }
    }

}
