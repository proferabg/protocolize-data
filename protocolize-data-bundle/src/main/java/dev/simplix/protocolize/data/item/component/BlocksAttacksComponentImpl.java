package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.BlocksAttacksComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.DamageReduction;
import dev.simplix.protocolize.api.item.objects.ItemDamageFunction;
import dev.simplix.protocolize.api.item.objects.SoundEvent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BlocksAttacksComponentImpl implements BlocksAttacksComponent {

    private float blockDelaySeconds;
    private float disableCooldownScale;
    private List<DamageReduction> damageReductions;
    private ItemDamageFunction itemDamage;
    private String bypassedBy;
    private SoundEvent blockSound;
    private SoundEvent disableSound;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        blockDelaySeconds = byteBuf.readFloat();
        disableCooldownScale = byteBuf.readFloat();
        int count = ProtocolUtil.readVarInt(byteBuf);
        damageReductions = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            damageReductions.add(DataComponentUtil.readDamageReduction(byteBuf, protocolVersion));
        }
        itemDamage = DataComponentUtil.readItemDamageFunction(byteBuf, protocolVersion);
        if(byteBuf.readBoolean()){
            bypassedBy = ProtocolUtil.readString(byteBuf);
        }
        if(byteBuf.readBoolean()){
            blockSound = DataComponentUtil.readSoundEvent(byteBuf, protocolVersion);
        }
        if(byteBuf.readBoolean()){
            disableSound = DataComponentUtil.readSoundEvent(byteBuf, protocolVersion);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        byteBuf.writeFloat(blockDelaySeconds);
        byteBuf.writeFloat(disableCooldownScale);
        ProtocolUtil.writeVarInt(byteBuf, damageReductions.size());
        for(DamageReduction damageReduction : damageReductions){
            DataComponentUtil.writeDamageReduction(byteBuf, protocolVersion, damageReduction);
        }
        DataComponentUtil.writeItemDamageFunction(byteBuf, protocolVersion, itemDamage);
        byteBuf.writeBoolean(bypassedBy != null);
        if(bypassedBy != null){
            ProtocolUtil.writeString(byteBuf, bypassedBy);
        }
        byteBuf.writeBoolean(blockSound != null);
        if(blockSound != null){
            DataComponentUtil.writeSoundEvent(byteBuf, protocolVersion, blockSound);
        }
        if(disableSound != null){
            DataComponentUtil.writeSoundEvent(byteBuf, protocolVersion, disableSound);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<BlocksAttacksComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:blocks_attacks";
        }

        @Override
        public BlocksAttacksComponent createEmpty() {
            return new BlocksAttacksComponentImpl(0, 0, new ArrayList<>(), null, null, null, null);
        }

        @Override
        public BlocksAttacksComponent create(float blockDelaySeconds, float disableCooldownScale, List<DamageReduction> damageReductions, ItemDamageFunction itemDamage, String bypassedBy, SoundEvent blockSound, SoundEvent disableSound) {
            return new BlocksAttacksComponentImpl(blockDelaySeconds, disableCooldownScale, damageReductions, itemDamage, bypassedBy, blockSound, disableSound);
        }
    }

}
