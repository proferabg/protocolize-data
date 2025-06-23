package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.PotionDurationScaleComponent;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class PotionDurationScaleComponentImpl implements PotionDurationScaleComponent {

    float scale;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        scale = byteBuf.readFloat();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        byteBuf.writeFloat(scale);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<PotionDurationScaleComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:potion_duration_scale";
        }

        @Override
        public PotionDurationScaleComponent createEmpty() {
            return new PotionDurationScaleComponentImpl(0);
        }

        @Override
        public PotionDurationScaleComponent create(float scale) {
            return new PotionDurationScaleComponentImpl(scale);
        }
    }

}
