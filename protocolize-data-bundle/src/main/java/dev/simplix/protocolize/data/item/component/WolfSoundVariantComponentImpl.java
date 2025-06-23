package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.WolfSoundVariantComponent;
import dev.simplix.protocolize.data.WolfSoundVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class WolfSoundVariantComponentImpl implements WolfSoundVariantComponent {

    WolfSoundVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, WolfSoundVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<WolfSoundVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:wolf_sound_variant";
        }

        @Override
        public WolfSoundVariantComponent createEmpty() {
            return new WolfSoundVariantComponentImpl(null);
        }

        @Override
        public WolfSoundVariantComponent create(WolfSoundVariant variant) {
            return new WolfSoundVariantComponentImpl(variant);
        }
    }

}
