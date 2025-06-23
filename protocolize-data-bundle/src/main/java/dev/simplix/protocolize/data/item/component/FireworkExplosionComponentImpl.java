package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.FireworkExplosionComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.Firework;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class FireworkExplosionComponentImpl implements FireworkExplosionComponent {

    private Firework.Meta explosion;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        explosion = DataComponentUtil.readFireworkMeta(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        DataComponentUtil.writeFireworkMeta(byteBuf, protocolVersion, explosion);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<FireworkExplosionComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public FireworkExplosionComponent create(Firework.Meta explosion) {
            return new FireworkExplosionComponentImpl(explosion);
        }

        @Override
        public String getName() {
            return "minecraft:firework_explosion";
        }

        @Override
        public FireworkExplosionComponent createEmpty() {
            return new FireworkExplosionComponentImpl(new Firework.Meta(Firework.Meta.Shape.SMALL_BALL, new ArrayList<>(0), new ArrayList<>(0), false, false));
        }

    }

}
