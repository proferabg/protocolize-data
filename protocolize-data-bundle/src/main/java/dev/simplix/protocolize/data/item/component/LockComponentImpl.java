package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.LockComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

@Data
@AllArgsConstructor
public class LockComponentImpl implements LockComponent {

    private String key;
    private CompoundTag data;

    // This is a work in progress that is not finished
    //private ItemPredicate predicate;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
//        if(protocolVersion >= MINECRAFT_1_21_5) {
//            predicate = ItemPredicateImpl.Type.INSTANCE.createEmpty();
//            predicate.read(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion), protocolVersion);
//        } else
        if(protocolVersion >= MINECRAFT_1_21_2) {
            data = (CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
        } else {
            StringTag lock = (StringTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
            if(lock != null){
                key = lock.getValue();
            }
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
//        if(protocolVersion >= MINECRAFT_1_21_5) {
//            if(predicate != null){
//                NamedBinaryTagUtil.writeTag(byteBuf, predicate.write(protocolVersion, "lock", new CompoundTag()), protocolVersion);
//            }
//        } else
         if(protocolVersion >= MINECRAFT_1_21_2) {
            if(data == null){
                data = new CompoundTag();
            }
            NamedBinaryTagUtil.writeTag(byteBuf, data, protocolVersion);
        } else {
            if(key == null) {
                key = "";
            }
            NamedBinaryTagUtil.writeTag(byteBuf, new StringTag(key), protocolVersion);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<LockComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        @Deprecated
        public LockComponent create(String key) {
            return new LockComponentImpl(key, null);
        }

        @Override
        @Deprecated
        public LockComponent create(CompoundTag data) {
            return new LockComponentImpl(null, data);
        }

        @Override
        public String getName() {
            return "minecraft:lock";
        }

        @Override
        public LockComponent createEmpty() {
            return new LockComponentImpl(null, null);
        }

    }

}
