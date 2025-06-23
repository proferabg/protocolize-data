package dev.simplix.protocolize.data.item.component;

import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.TooltipDisplayComponent;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.exception.InvalidDataComponentTypeException;
import dev.simplix.protocolize.api.util.exception.InvalidDataComponentVersionException;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TooltipDisplayComponentImpl implements TooltipDisplayComponent {

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    private boolean hideTooltip;
    private List<DataComponentType<?>> hiddenComponents;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        hideTooltip = byteBuf.readBoolean();
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            hiddenComponents.add(readComponentType(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        byteBuf.writeBoolean(hideTooltip);
        ProtocolUtil.writeVarInt(byteBuf, hiddenComponents.size());
        for(DataComponentType<?> component : hiddenComponents) {
            writeComponentType(byteBuf, component, protocolVersion);
        }
    }

    @Override
    public void addHiddenComponent(DataComponentType<?> dataComponentType) {
        hiddenComponents.add(dataComponentType);
    }

    @Override
    public void removeHiddenComponent(DataComponentType<?> dataComponentType) {
        hiddenComponents.remove(dataComponentType);
    }

    @Override
    public void clearHiddenComponents() {
        hiddenComponents.clear();
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<TooltipDisplayComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:tooltip_display";
        }

        @Override
        public TooltipDisplayComponent createEmpty() {
            return new TooltipDisplayComponentImpl(false, new ArrayList<>());
        }

        @Override
        public TooltipDisplayComponent create(boolean hideTooltip, List<DataComponentType<?>> hiddenComponents) {
            return new TooltipDisplayComponentImpl(hideTooltip, hiddenComponents);
        }
    }

    private DataComponentType<?> readComponentType(ByteBuf buf, int protocolVersion) {
        int componentId = ProtocolUtil.readVarInt(buf);
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

    private void writeComponentType(ByteBuf buf, DataComponentType<?> componentType, int protocolVersion) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(componentType, protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            throw new InvalidDataComponentVersionException(componentType, protocolVersion);
        }
        ProtocolUtil.writeVarInt(buf, ((ProtocolIdMapping) mapping).id());
    }
}
