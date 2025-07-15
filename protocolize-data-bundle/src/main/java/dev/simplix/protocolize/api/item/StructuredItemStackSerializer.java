package dev.simplix.protocolize.api.item;

import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.*;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.DebugUtil;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.exception.InvalidDataComponentTypeException;
import dev.simplix.protocolize.api.util.exception.InvalidDataComponentVersionException;
import dev.simplix.protocolize.data.ItemType;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "Protocolize")
public final class StructuredItemStackSerializer {

    private static final List<Integer> UNKNOWN_ITEMS = new ArrayList<>();
    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    public ItemStack read(ByteBuf buf, int protocolVersion) {
        StringBuilder sb = new StringBuilder();
        sb.append("StructuredItemStackSerializer:");
        sb.append("\n    Available Bytes: 0x").append(Integer.toHexString(buf.readableBytes()).toUpperCase());
        try {
            int amount = ProtocolUtil.readVarInt(buf);
            sb.append("\n    Item Count: 0x").append(Integer.toHexString(amount).toUpperCase());
            if (amount == 0) {
                return ItemStack.NO_DATA;
            }
            int itemId = ProtocolUtil.readVarInt(buf);
            sb.append("\n    Item ID: 0x").append(Integer.toHexString(itemId).toUpperCase());
            ItemType type = findItemType(itemId, protocolVersion);
            if (type == null && !UNKNOWN_ITEMS.contains(itemId)) { //prevent console spam by checking if already logged
                UNKNOWN_ITEMS.add(itemId);
                log.warn("Don't know what item {} at protocol {} should be.", itemId, protocolVersion);
            }
            sb.append("\n    Item Type: ").append(type != null ? type.name() : "null");
            int toAdd = ProtocolUtil.readVarInt(buf);
            sb.append("\n    Components(+): 0x").append(Integer.toHexString(toAdd).toUpperCase());
            int toRemove = ProtocolUtil.readVarInt(buf);
            sb.append("\n    Components(-): 0x").append(Integer.toHexString(toRemove).toUpperCase());
            List<DataComponent> componentsToAdd = new ArrayList<>(toAdd);
            List<DataComponentType<?>> componentsToRemove = new ArrayList<>(toRemove);
            for (int i = 0; i < toAdd; i++) {
                componentsToAdd.add(readComponent(buf, protocolVersion, sb));
            }
            for (int i = 0; i < toRemove; i++) {
                componentsToRemove.add(readComponentType(buf, protocolVersion, sb));
            }
            ItemStack out = new ItemStack(type, amount);
            componentsToAdd.forEach(out::addComponent);
            componentsToRemove.forEach(out::removeComponent);
            populateLoreAndDisplayNameFromComponents(out);
            return out;
        } catch (Exception e) {
            if(DebugUtil.enabled) log.info(sb.toString());
            throw e;
        }
    }

    public void write(ByteBuf buf, BaseItemStack stack, int protocolVersion) {
        if (stack.itemType() == null) {
            ProtocolUtil.writeVarInt(buf, 0);
            return;
        }
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(stack.itemType(), protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            log.warn("{} cannot be used on protocol version {}", stack.itemType().name(), protocolVersion);
            ProtocolUtil.writeVarInt(buf, 0);
            return;
        }
        ProtocolUtil.writeVarInt(buf, stack.amount());
        if (stack.amount() == 0) {
            return;
        }
        populateComponentsFromDisplayNameAndLore(stack);
        ProtocolUtil.writeVarInt(buf, ((ProtocolIdMapping) mapping).id());
        ProtocolUtil.writeVarInt(buf, stack.getComponents().size());
        ProtocolUtil.writeVarInt(buf, stack.getComponentsToRemove().size());
        for (DataComponent component : stack.getComponents()) {
            writeComponent(buf, component, protocolVersion);
        }
        for (DataComponentType<?> type : stack.getComponentsToRemove()) {
            writeComponentType(buf, type, protocolVersion);
        }
    }

    private void populateComponentsFromDisplayNameAndLore(BaseItemStack stack) {
        if (stack.displayName() != null) {
            stack.addComponent(CustomNameComponent.create(stack.displayName().disableItalic()));
        }
        if (stack.lore() != null && !stack.lore().isEmpty()) {
            stack.addComponent(LoreComponent.create(stack.lore().stream().
                map(ChatElement::disableItalic)
                .collect(Collectors.toList())));
        }
    }

    private void writeComponent(ByteBuf buf, DataComponent component, int protocolVersion) {
        writeComponentType(buf, component.getType(), protocolVersion);
        try {
            component.write(buf, protocolVersion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to write " + component.getClass().getSimpleName() + " of item", e);
        }
    }

    private void writeComponentType(ByteBuf buf, DataComponentType<?> componentType, int protocolVersion) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(componentType, protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            throw new InvalidDataComponentVersionException(componentType, protocolVersion);
        }
        ProtocolUtil.writeVarInt(buf, ((ProtocolIdMapping) mapping).id());
    }

    private void populateLoreAndDisplayNameFromComponents(ItemStack itemStack) {
        ItemNameComponent itemNameComponent = itemStack.getComponent(ItemNameComponent.class);
        if (itemNameComponent != null) {
            itemStack.displayName(itemNameComponent.getName());
        }
        CustomNameComponent customNameComponent = itemStack.getComponent(CustomNameComponent.class);
        if (customNameComponent != null) {
            itemStack.displayName(customNameComponent.getCustomName());
        }
        LoreComponent loreComponent = itemStack.getComponent(LoreComponent.class);
        if (loreComponent != null) {
            itemStack.lore(loreComponent.getLore());
        }
    }

    private DataComponent readComponent(ByteBuf buf, int protocolVersion, StringBuilder sb) {
        DataComponentType<?> type = readComponentType(buf, protocolVersion, sb);
        sb.append("\n    Component Type: ").append(type.getName());
        DataComponent component = type.createEmpty();
        try {
            component.read(buf, protocolVersion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read '" + component.getType().getName() + "' data component for item at protocol version " + protocolVersion, e);
        }
        return component;
    }

    private DataComponentType<?> readComponentType(ByteBuf buf, int protocolVersion, StringBuilder sb) {
        int componentId = ProtocolUtil.readVarInt(buf);
        sb.append("\n    Component ID: 0x").append(Integer.toHexString(componentId).toUpperCase());
        return findComponentType(componentId, protocolVersion);
    }

    private DataComponentType<?> findComponentType(int componentId, int protocolVersion) {
        Multimap<DataComponentType, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(DataComponentType.class, protocolVersion);
        for (DataComponentType<?> type : mappings.keySet()) {
            for (ProtocolMapping mapping : mappings.get(type)) {
                if (mapping instanceof ProtocolIdMapping && ((ProtocolIdMapping) mapping).id() == componentId) {
                    return type;
                }
            }
        }
        throw new InvalidDataComponentTypeException(componentId, protocolVersion);
    }

    public static ItemType findItemType(int id, int protocolVersion) {
        Multimap<ItemType, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(ItemType.class, protocolVersion);
        for (ItemType type : mappings.keySet()) {
            for (ProtocolMapping mapping : mappings.get(type)) {
                if (mapping instanceof ProtocolIdMapping) {
                    if (((ProtocolIdMapping) mapping).id() == id) {
                        return type;
                    }
                }
            }
        }
        return null;
    }

}
