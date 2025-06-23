package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.EquippableComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.enums.EquipmentSlot;
import dev.simplix.protocolize.api.item.objects.HolderSet;
import dev.simplix.protocolize.api.item.objects.SoundEvent;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.EntityType;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquippableComponentImpl implements EquippableComponent {

    private EquipmentSlot slot;
    private SoundEvent equipSound;
    private String model;
    private String cameraOverlay;
    private HolderSet<EntityType> allowedEntities;
    private boolean dispensable;
    private boolean swappable;
    private boolean damageOnHurt;
    private boolean equipOnInteract;
    private boolean canBeSheared;
    private SoundEvent shearingSound;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        slot = EquipmentSlot.values()[ProtocolUtil.readVarInt(byteBuf)];
        equipSound = DataComponentUtil.readSoundEvent(byteBuf, protocolVersion);
        if(byteBuf.readBoolean()) {
            model = ProtocolUtil.readString(byteBuf);
        }
        if(byteBuf.readBoolean()) {
            cameraOverlay = ProtocolUtil.readString(byteBuf);
        }
        if(byteBuf.readBoolean()) {
            allowedEntities = DataComponentUtil.readHolderSet(byteBuf, protocolVersion, EntityType.class);
        }
        dispensable = byteBuf.readBoolean();
        swappable = byteBuf.readBoolean();
        damageOnHurt = byteBuf.readBoolean();
        if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5) {
            equipOnInteract = byteBuf.readBoolean();
        }
        if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_6) {
            canBeSheared = byteBuf.readBoolean();
            shearingSound = DataComponentUtil.readSoundEvent(byteBuf, protocolVersion);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, slot.ordinal());
        DataComponentUtil.writeSoundEvent(byteBuf, protocolVersion, equipSound);
        byteBuf.writeBoolean(model != null);
        if(model != null) {
            ProtocolUtil.writeString(byteBuf, model);
        }
        byteBuf.writeBoolean(cameraOverlay != null);
        if(cameraOverlay != null) {
            ProtocolUtil.writeString(byteBuf, cameraOverlay);
        }
        byteBuf.writeBoolean(allowedEntities != null);
        if(allowedEntities != null) {
            DataComponentUtil.writeHolderSet(byteBuf, protocolVersion, allowedEntities, EntityType.class);
        }
        byteBuf.writeBoolean(dispensable);
        byteBuf.writeBoolean(swappable);
        byteBuf.writeBoolean(damageOnHurt);
        if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5) {
            byteBuf.writeBoolean(equipOnInteract);
        }
        if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_6) {
            byteBuf.writeBoolean(canBeSheared);
            DataComponentUtil.writeSoundEvent(byteBuf, protocolVersion, shearingSound);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<EquippableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public EquippableComponent create(EquipmentSlot equipmentSlot, SoundEvent equipSound, String model, String cameraOverlay, HolderSet<EntityType> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt, boolean equipOnInteract, boolean canBeSheared, SoundEvent shearingSound) {
            return new EquippableComponentImpl(equipmentSlot, equipSound, model, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt, equipOnInteract, canBeSheared, shearingSound);
        }

        @Override
        public String getName() {
            return "minecraft:equippable";
        }

        @Override
        public EquippableComponent createEmpty() {
            return create(EquipmentSlot.MAINHAND, null, null, null, null, false, false, false, false, false, null);
        }

    }

}
