package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.WeaponComponent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class WeaponComponentImpl implements WeaponComponent {

    private int itemDamagePerAttack;
    private float disableBlockingForSeconds;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        itemDamagePerAttack = ProtocolUtil.readVarInt(byteBuf);
        disableBlockingForSeconds = byteBuf.readFloat();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        ProtocolUtil.writeVarInt(byteBuf, itemDamagePerAttack);
        byteBuf.writeFloat(disableBlockingForSeconds);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<WeaponComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:weapon";
        }

        @Override
        public WeaponComponent createEmpty() {
            return new WeaponComponentImpl(0, 0);
        }

        @Override
        public WeaponComponent create(int damagePerAttack, float disableBlockingForSeconds) {
            return new WeaponComponentImpl(damagePerAttack, disableBlockingForSeconds);
        }
    }

}
