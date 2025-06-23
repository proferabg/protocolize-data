package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.EnchantableComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnchantableComponentImpl implements EnchantableComponent {

    private int value;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        value = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, value);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<EnchantableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public EnchantableComponent create(int id) {
            return new EnchantableComponentImpl(id);
        }

        @Override
        public String getName() {
            return "minecraft:enchantable";
        }

        @Override
        public EnchantableComponent createEmpty() {
            return new EnchantableComponentImpl(0);
        }

    }

}
