package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.BannerPatternsComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.enums.DyeColor;
import dev.simplix.protocolize.api.item.objects.BannerPatternLayer;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.BannerPattern;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BannerPatternsComponentImpl implements BannerPatternsComponent {

    private List<BannerPatternLayer> layers;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for (int i = 0; i < count; i++) {
            BannerPatternLayer layer = new BannerPatternLayer();
            layer.setPattern(DataComponentUtil.readHolder(byteBuf, protocolVersion, BannerPattern.class, DataComponentUtil::readBannerPattern));
            layer.setColor(DyeColor.values()[ProtocolUtil.readVarInt(byteBuf)]);
            layers.add(layer);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, layers.size());
        for (BannerPatternLayer layer : layers) {
            DataComponentUtil.writeHolder(byteBuf, protocolVersion, layer.getPattern(), BannerPattern.class, DataComponentUtil::writeBannerPattern);
            ProtocolUtil.writeVarInt(byteBuf, layer.getColor().ordinal());
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addLayer(BannerPatternLayer layer) {
        layers.add(layer);
    }

    @Override
    public void removeLayer(BannerPatternLayer layer) {
        layers.remove(layer);
    }

    @Override
    public void removeAllLayers() {
        layers.clear();
    }

    public static class Type implements DataComponentType<BannerPatternsComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public BannerPatternsComponent create(List<BannerPatternLayer> layers) {
            return new BannerPatternsComponentImpl(layers);
        }

        @Override
        public String getName() {
            return "minecraft:banner_patterns";
        }


        @Override
        public BannerPatternsComponent createEmpty() {
            return new BannerPatternsComponentImpl(new ArrayList<>(0));
        }

    }

}
