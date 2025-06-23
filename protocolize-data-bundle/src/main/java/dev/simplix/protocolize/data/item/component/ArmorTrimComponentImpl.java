package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.ArmorTrimComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.DirectTrimMaterial;
import dev.simplix.protocolize.api.item.objects.DirectTrimPattern;
import dev.simplix.protocolize.api.item.objects.Holder;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.TrimMaterial;
import dev.simplix.protocolize.data.TrimPattern;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class ArmorTrimComponentImpl implements ArmorTrimComponent {

    Holder<TrimMaterial, DirectTrimMaterial> trimMaterial;
    Holder<TrimPattern, DirectTrimPattern> trimPattern;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        trimMaterial = DataComponentUtil.readHolder(byteBuf, protocolVersion, TrimMaterial.class, DataComponentUtil::readTrimMaterial);
        trimPattern = DataComponentUtil.readHolder(byteBuf, protocolVersion, TrimPattern.class, DataComponentUtil::readTrimPattern);
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4){
            showInTooltip = byteBuf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        DataComponentUtil.writeHolder(byteBuf, protocolVersion, trimMaterial, TrimMaterial.class, DataComponentUtil::writeTrimMaterial);
        DataComponentUtil.writeHolder(byteBuf, protocolVersion, trimPattern, TrimPattern.class, DataComponentUtil::writeTrimPattern);
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4){
            byteBuf.writeBoolean(showInTooltip);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<ArmorTrimComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        @Deprecated
        public ArmorTrimComponent create(Holder<TrimMaterial, DirectTrimMaterial> trimMaterial, Holder<TrimPattern, DirectTrimPattern> trimPattern, boolean showInTooltip) {
            return new ArmorTrimComponentImpl(trimMaterial, trimPattern, showInTooltip);
        }

        @Override
        public ArmorTrimComponent create(Holder<TrimMaterial, DirectTrimMaterial> trimMaterial, Holder<TrimPattern, DirectTrimPattern> trimPattern) {
            return new ArmorTrimComponentImpl(trimMaterial, trimPattern, false);
        }

        @Override
        public String getName() {
            return "minecraft:trim";
        }

        @Override
        public ArmorTrimComponent createEmpty() {
            return new ArmorTrimComponentImpl(null, null, false);
        }

    }

}
