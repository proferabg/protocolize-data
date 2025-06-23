package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CustomModelDataComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21_4;

@Data
@AllArgsConstructor
public class CustomModelDataComponentImpl implements CustomModelDataComponent {

    private int customModelData;
    private List<Float> floats;
    private List<Boolean> flags;
    private List<String> strings;
    private List<Integer> colors;


    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if (protocolVersion >= MINECRAFT_1_21_4) {
            int count = ProtocolUtil.readVarInt(byteBuf);
            for (int i = 0; i < count; i++) {
                floats.add(byteBuf.readFloat());
            }
            count = ProtocolUtil.readVarInt(byteBuf);
            for (int i = 0; i < count; i++) {
                flags.add(byteBuf.readBoolean());
            }
            count = ProtocolUtil.readVarInt(byteBuf);
            for (int i = 0; i < count; i++) {
                strings.add(ProtocolUtil.readString(byteBuf));
            }
            count = ProtocolUtil.readVarInt(byteBuf);
            for (int i = 0; i < count; i++) {
                colors.add(byteBuf.readInt());
            }
        } else {
            customModelData = ProtocolUtil.readVarInt(byteBuf);
        }

    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if (protocolVersion >= MINECRAFT_1_21_4) {
            ProtocolUtil.writeVarInt(byteBuf, floats.size());
            for(Float f : floats) {
                byteBuf.writeFloat(f);
            }
            ProtocolUtil.writeVarInt(byteBuf, flags.size());
            for(Boolean b : flags) {
                byteBuf.writeBoolean(b);
            }
            ProtocolUtil.writeVarInt(byteBuf, strings.size());
            for(String s : strings) {
                ProtocolUtil.writeString(byteBuf, s);
            }
            ProtocolUtil.writeVarInt(byteBuf, colors.size());
            for(Integer c : colors) {
                byteBuf.writeInt(c);
            }
        } else {
            ProtocolUtil.writeVarInt(byteBuf, customModelData);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addFloat(float value) {
        floats.add(value);
    }

    @Override
    public void removeFloat(float value) {
        floats.remove(value);
    }

    @Override
    public void clearFloats() {
        floats.clear();
    }

    @Override
    public void addFlag(boolean value) {
        flags.add(value);
    }

    @Override
    public void removeFlag(boolean value) {
        flags.remove(value);
    }

    @Override
    public void clearFlags() {
        flags.clear();
    }

    @Override
    public void addString(String value) {
        strings.add(value);
    }

    @Override
    public void removeString(String value) {
        strings.remove(value);
    }

    @Override
    public void clearStrings() {
        strings.clear();
    }

    @Override
    public void addColor(Integer value) {
        colors.add(value);
    }

    @Override
    public void removeColor(Integer value) {
        colors.remove(value);
    }

    @Override
    public void clearColors() {
        colors.clear();
    }

    public static class Type implements DataComponentType<CustomModelDataComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public CustomModelDataComponent create(int customModelData) {
            return new CustomModelDataComponentImpl(customModelData, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        @Override
        public CustomModelDataComponent create(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
            return new CustomModelDataComponentImpl(0, floats, flags, strings, colors);
        }

        @Override
        public String getName() {
            return "minecraft:custom_model_data";
        }

        @Override
        public CustomModelDataComponent createEmpty() {
            return new CustomModelDataComponentImpl(0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

}
