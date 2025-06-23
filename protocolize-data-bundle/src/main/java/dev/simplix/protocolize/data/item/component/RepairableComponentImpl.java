package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.RepairableComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.HolderSet;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class RepairableComponentImpl implements RepairableComponent {

    private HolderSet<ItemType> items;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        items = DataComponentUtil.readHolderSet(byteBuf, protocolVersion, ItemType.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        DataComponentUtil.writeHolderSet(byteBuf, protocolVersion, items, ItemType.class);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<RepairableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public RepairableComponent create(List<ItemType> items) {
            return new RepairableComponentImpl(new HolderSet<>(items));
        }

        @Override
        public RepairableComponent create(String itemResourceLocation) {
            return new RepairableComponentImpl(new HolderSet<>(itemResourceLocation));
        }

        @Override
        public String getName() {
            return "minecraft:repairable";
        }

        @Override
        public RepairableComponent createEmpty() {
            return new RepairableComponentImpl(new HolderSet<>(new ArrayList<>(0)));
        }

    }
}
