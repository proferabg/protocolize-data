package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.DataComponentType;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j(topic = "Protocolize")
public class HashedStackSerializer {

    private static final List<Integer> UNKNOWN_ITEMS = new ArrayList<>();
    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    public static HashedStack read(ByteBuf buf, int protocolVersion) {
        if(!buf.readBoolean()) {
            return new HashedStack(Optional.empty());
        } else {
            HashedStack.ActualItem item = new HashedStack.ActualItem();
            int itemId = ProtocolUtil.readVarInt(buf);
            ItemType type = StructuredItemStackSerializer.findItemType(itemId, protocolVersion);
            if (type == null && !UNKNOWN_ITEMS.contains(itemId)) {
                UNKNOWN_ITEMS.add(itemId);
                log.warn("Don't know what item {} at protocol {} should be.", itemId, protocolVersion);
            }
            item.setType(type);
            item.setCount(ProtocolUtil.readVarInt(buf));
            Map<DataComponentType, Integer> addedComponents = new HashMap<>();
            Set<DataComponentType> removedComponents = new HashSet<>();
            int count = ProtocolUtil.readVarInt(buf);
            for (int i = 0; i < count; i++) {
                addedComponents.put(DataComponentUtil.readRegistry(buf, protocolVersion, DataComponentType.class), buf.readInt());
            }
            count = ProtocolUtil.readVarInt(buf);
            for (int i = 0; i < count; i++) {
                removedComponents.add(DataComponentUtil.readRegistry(buf, protocolVersion, DataComponentType.class));
            }
            item.setComponents(new HashedStack.HashedPatchMap(addedComponents, removedComponents));
            return new HashedStack(Optional.of(item));
        }
    }

    public static void write(ByteBuf buf, HashedStack stack, int protocolVersion) {
        buf.writeBoolean(stack.getItem().isPresent());
        if(stack.getItem().isPresent()){
            HashedStack.ActualItem item = stack.getItem().get();
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(item.getType(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", item.getType().name(), protocolVersion);
                ProtocolUtil.writeVarInt(buf, 0);
                return;
            }
            ProtocolUtil.writeVarInt(buf, ((ProtocolIdMapping) mapping).id());
            ProtocolUtil.writeVarInt(buf, item.getCount());
            ProtocolUtil.writeVarInt(buf, item.getComponents().getAddedComponents().size());
            for (Map.Entry<DataComponentType, Integer> entry : item.getComponents().getAddedComponents().entrySet()) {
                DataComponentUtil.writeRegistry(buf, protocolVersion, entry.getKey());
                buf.writeInt(entry.getValue());
            }
            ProtocolUtil.writeVarInt(buf, item.getComponents().getRemovedComponents().size());
            for (DataComponentType removedComponent : item.getComponents().getRemovedComponents()) {
                DataComponentUtil.writeRegistry(buf, protocolVersion, removedComponent);
            }
        }
    }
}
