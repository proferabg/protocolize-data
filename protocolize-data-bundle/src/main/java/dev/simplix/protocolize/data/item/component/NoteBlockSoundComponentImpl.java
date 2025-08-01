package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.NoteBlockSoundComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoteBlockSoundComponentImpl implements NoteBlockSoundComponent {

    private String sound;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        sound = ProtocolUtil.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeString(byteBuf, sound);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<NoteBlockSoundComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public NoteBlockSoundComponent create(String sound) {
            return new NoteBlockSoundComponentImpl(sound);
        }

        @Override
        public String getName() {
            return "minecraft:note_block_sound";
        }

        @Override
        public NoteBlockSoundComponent createEmpty() {
            return new NoteBlockSoundComponentImpl("");
        }

    }

}
