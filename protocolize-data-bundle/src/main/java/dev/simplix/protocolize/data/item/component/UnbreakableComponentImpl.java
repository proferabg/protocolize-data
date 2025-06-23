package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.UnbreakableComponent;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnbreakableComponentImpl implements UnbreakableComponent {

    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            showInTooltip = byteBuf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            byteBuf.writeBoolean(showInTooltip);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<UnbreakableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        @Deprecated
        public UnbreakableComponent create(boolean showInTooltip) {
            return new UnbreakableComponentImpl(showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:unbreakable";
        }

        @Override
        public UnbreakableComponent createEmpty() {
            return new UnbreakableComponentImpl(true);
        }

    }

}
