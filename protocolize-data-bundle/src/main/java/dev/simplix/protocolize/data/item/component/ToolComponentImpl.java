package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.ToolComponent;
import dev.simplix.protocolize.api.item.objects.ToolRule;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.Block;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ToolComponentImpl implements ToolComponent {

    private List<ToolRule> rules;
    private float miningSpeed;
    private int damagePerBlock;
    private boolean canDestroyBlocksInCreative;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            rules.add(readRule(byteBuf, protocolVersion));
        }
        miningSpeed = byteBuf.readFloat();
        damagePerBlock = ProtocolUtil.readVarInt(byteBuf);
        if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5){
            canDestroyBlocksInCreative = byteBuf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, rules.size());
        for(ToolRule rule : rules) {
            writeRule(byteBuf, rule, protocolVersion);
        }
        byteBuf.writeFloat(miningSpeed);
        ProtocolUtil.writeVarInt(byteBuf, damagePerBlock);
        if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5){
            byteBuf.writeBoolean(canDestroyBlocksInCreative);
        }
    }

    private ToolRule readRule(ByteBuf byteBuf, int protocolVersion){
        ToolRule rule = new ToolRule();
        rule.setBlockSet(DataComponentUtil.readHolderSet(byteBuf, protocolVersion, Block.class));
        if(byteBuf.readBoolean()){
            rule.setSpeed(byteBuf.readFloat());
        }
        if(byteBuf.readBoolean()){
            rule.setCorrectToolForDrops(byteBuf.readBoolean());
        }
        return rule;
    }

    private void writeRule(ByteBuf byteBuf, ToolRule rule, int protocolVersion){
        DataComponentUtil.writeHolderSet(byteBuf, protocolVersion, rule.getBlockSet(), Block.class);
        boolean hasSpeed = rule.getSpeed() != null;
        byteBuf.writeBoolean(hasSpeed);
        if(hasSpeed){
            byteBuf.writeFloat(rule.getSpeed());
        }
        boolean hasCorrectToolForDrops = rule.getCorrectToolForDrops() != null;
        byteBuf.writeBoolean(hasCorrectToolForDrops);
        if(hasCorrectToolForDrops){
            byteBuf.writeBoolean(rule.getCorrectToolForDrops());
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addRule(ToolRule rule) {
        rules.add(rule);
    }

    @Override
    public void removeRule(ToolRule rule) {
        rules.remove(rule);
    }

    @Override
    public void removeAllRules() {
        rules.clear();
    }

    public static class Type implements DataComponentType<ToolComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public ToolComponent create(List<ToolRule> rules, float miningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative) {
            return new ToolComponentImpl(rules, miningSpeed, damagePerBlock, canDestroyBlocksInCreative);
        }

        @Override
        public String getName() {
            return "minecraft:tool";
        }

        @Override
        public ToolComponent createEmpty() {
            return new ToolComponentImpl(new ArrayList<>(0), 0, 0, false);
        }

    }

}
