package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.FireworksComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.Firework;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class FireworksComponentImpl implements FireworksComponent {

    private Firework firework;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        firework.setFlightDuration(ProtocolUtil.readVarInt(byteBuf));
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            firework.getExplosions().add(DataComponentUtil.readFireworkMeta(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, firework.getFlightDuration());
        ProtocolUtil.writeVarInt(byteBuf, firework.getExplosions().size());
        for(Firework.Meta meta : firework.getExplosions()) {
            DataComponentUtil.writeFireworkMeta(byteBuf, protocolVersion, meta);
        }
    }
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<FireworksComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public FireworksComponent create(Firework firework) {
            return new FireworksComponentImpl(firework);
        }

        @Override
        public String getName() {
            return "minecraft:fireworks";
        }

        @Override
        public FireworksComponent createEmpty() {
            return new FireworksComponentImpl(new Firework(0, new ArrayList<>(0)));
        }

    }

}
