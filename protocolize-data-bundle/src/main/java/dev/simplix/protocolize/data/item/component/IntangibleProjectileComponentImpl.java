package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.IntangibleProjectileComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

@Data
@AllArgsConstructor
public class IntangibleProjectileComponentImpl implements IntangibleProjectileComponent {

    CompoundTag data;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException{
        data = (CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        NamedBinaryTagUtil.writeTag(byteBuf, data, protocolVersion);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<IntangibleProjectileComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:intangible_projectile";
        }

        @Override
        public IntangibleProjectileComponent createEmpty() {
            return new IntangibleProjectileComponentImpl(new CompoundTag());
        }

        @Override
        public IntangibleProjectileComponent create() {
            return new IntangibleProjectileComponentImpl(new CompoundTag());
        }
    }

}
