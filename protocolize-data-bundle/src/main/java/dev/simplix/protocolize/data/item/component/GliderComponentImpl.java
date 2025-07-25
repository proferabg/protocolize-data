package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.GliderComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import io.netty.buffer.ByteBuf;

public class GliderComponentImpl implements GliderComponent {

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<GliderComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public GliderComponent create() {
            return new GliderComponentImpl();
        }

        @Override
        public String getName() {
            return "minecraft:glider";
        }

        @Override
        public GliderComponent createEmpty() {
            return create();
        }

    }

}
