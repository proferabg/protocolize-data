package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.FireResistantComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import io.netty.buffer.ByteBuf;

public class FireResistantComponentImpl implements FireResistantComponent {

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<FireResistantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public FireResistantComponent create() {
            return new FireResistantComponentImpl();
        }

        @Override
        public String getName() {
            return "minecraft:fire_resistant";
        }

        @Override
        public FireResistantComponent createEmpty() {
            return create();
        }

    }

}
