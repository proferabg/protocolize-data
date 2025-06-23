package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.CustomNameComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomNameComponentImpl implements CustomNameComponent {

    private ChatElement<?> customName;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        customName = ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        NamedBinaryTagUtil.writeTag(byteBuf, customName.asNbt(), protocolVersion);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<CustomNameComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public CustomNameComponent create(ChatElement<?> chatElement) {
            return new CustomNameComponentImpl(chatElement);
        }

        @Override
        public String getName() {
            return "minecraft:custom_name";
        }

        @Override
        public CustomNameComponent createEmpty() {
            return new CustomNameComponentImpl(null);
        }

    }

}
