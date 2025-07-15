package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.AttributeModifiersComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.enums.EquipmentSlotGroup;
import dev.simplix.protocolize.api.item.objects.AttributeModifier;
import dev.simplix.protocolize.api.item.objects.HolderSet;
import dev.simplix.protocolize.api.item.objects.ItemAttributeModifier;
import dev.simplix.protocolize.api.item.objects.MinMaxBounds;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.Attribute;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class AttributeModifiersComponentImpl implements AttributeModifiersComponent {

    private List<ItemAttributeModifier> attributeModifiers;
    private boolean showInTooltip;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        int attributeCount = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < attributeCount; i++){
            ItemAttributeModifier attributeModifier = new ItemAttributeModifier();
            attributeModifier.setType(new HolderSet<>(MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, Attribute.class)));
            if(protocolVersion < MINECRAFT_1_21){
                attributeModifier.setUuid(ProtocolUtil.readUniqueId(byteBuf));
            }
            AttributeModifier modifier = new AttributeModifier();
            modifier.setId(ProtocolUtil.readString(byteBuf));
            modifier.setAmount(byteBuf.readDouble());
            modifier.setOperation(AttributeModifier.Operation.values()[ProtocolUtil.readVarInt(byteBuf)]);
            attributeModifier.setModifier(modifier);
            attributeModifier.setSlot(EquipmentSlotGroup.values()[ProtocolUtil.readVarInt(byteBuf)]);
            attributeModifiers.add(attributeModifier);
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_6) {
                int displayType =  ProtocolUtil.readVarInt(byteBuf);
                switch (displayType) {
                    case 1:
                        attributeModifier.setDisplay(new ItemAttributeModifier.Display.Hidden());
                        break;
                    case 2:
                        attributeModifier.setDisplay(new ItemAttributeModifier.Display.Override(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion))));
                        break;
                    case 0:
                    default:
                        attributeModifier.setDisplay(new ItemAttributeModifier.Display.Default());
                        break;

                }
            }
        }
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            showInTooltip = byteBuf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        ProtocolUtil.writeVarInt(byteBuf, attributeModifiers.size());
        for(ItemAttributeModifier attributeModifier : attributeModifiers){
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(attributeModifier.getType().getSet().get(0), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                DataComponentUtil.logMappingWarning(attributeModifier.getType().getSet().get(0).name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
            if(protocolVersion < MINECRAFT_1_21){
                ProtocolUtil.writeUniqueId(byteBuf, attributeModifier.getUuid());
            }
            ProtocolUtil.writeString(byteBuf, attributeModifier.getModifier().getId());
            byteBuf.writeDouble(attributeModifier.getModifier().getAmount());
            ProtocolUtil.writeVarInt(byteBuf, attributeModifier.getModifier().getOperation().ordinal());
            ProtocolUtil.writeVarInt(byteBuf, attributeModifier.getSlot().ordinal());
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_6) {
                ProtocolUtil.writeVarInt(byteBuf, attributeModifier.getDisplay().getId());
                if(attributeModifier.getDisplay() instanceof ItemAttributeModifier.Display.Override){
                    NamedBinaryTagUtil.writeTag(byteBuf, ((ItemAttributeModifier.Display.Override) attributeModifier.getDisplay()).getText().asNbt(), protocolVersion);
                }
            }
        }
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            byteBuf.writeBoolean(showInTooltip);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addAttribute(ItemAttributeModifier attributeModifier) {
        attributeModifiers.add(attributeModifier);
    }

    @Override
    public void removeAttribute(ItemAttributeModifier attributeModifier) {
        attributeModifiers.remove(attributeModifier);
    }

    @Override
    public void removeAllAttributes() {
        attributeModifiers.clear();
    }

    public static class Type implements DataComponentType<AttributeModifiersComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:attribute_modifiers";
        }

        @Override
        public AttributeModifiersComponent createEmpty() {
            return new AttributeModifiersComponentImpl(new ArrayList<>(), true);
        }

        @Override
        public AttributeModifiersComponent create(List<ItemAttributeModifier> attributes) {
            return new AttributeModifiersComponentImpl(attributes, true);
        }

        @Override
        public AttributeModifiersComponent create(List<ItemAttributeModifier> attributes, boolean showInTooltip) {
            return new AttributeModifiersComponentImpl(attributes, showInTooltip);
        }
    }

}
