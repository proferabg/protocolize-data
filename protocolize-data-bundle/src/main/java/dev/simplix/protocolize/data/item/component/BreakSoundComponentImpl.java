package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.BreakSoundComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.SoundEvent;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class BreakSoundComponentImpl implements BreakSoundComponent {

    SoundEvent sound;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        sound = DataComponentUtil.readSoundEvent(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeSoundEvent(byteBuf, protocolVersion, sound);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<BreakSoundComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:break_sound";
        }

        @Override
        public BreakSoundComponent createEmpty() {
            return new BreakSoundComponentImpl(null);
        }

        @Override
        public BreakSoundComponent create(SoundEvent sound) {
            return new BreakSoundComponentImpl(sound);
        }
    }

}
