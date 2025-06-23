package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CanBreakComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.BlockPredicate;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CanBreakComponentImpl implements CanBreakComponent {

    private List<BlockPredicate> blockPredicates;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            blockPredicates.add(DataComponentUtil.readBlockPredicate(byteBuf, protocolVersion));
        }
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            showInTooltip = byteBuf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, blockPredicates.size());
        for(BlockPredicate blockPredicate : blockPredicates) {
            DataComponentUtil.writeBlockPredicate(byteBuf, protocolVersion, blockPredicate);
        }
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            byteBuf.writeBoolean(showInTooltip);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<CanBreakComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public CanBreakComponent create(List<BlockPredicate> blockPredicates, boolean showInTooltip) {
            return new CanBreakComponentImpl(blockPredicates, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:can_break";
        }

        @Override
        public CanBreakComponent createEmpty() {
            return new CanBreakComponentImpl(new ArrayList<>(0), true);
        }

    }

}
