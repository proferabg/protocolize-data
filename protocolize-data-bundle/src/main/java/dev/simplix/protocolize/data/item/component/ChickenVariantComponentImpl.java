package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.ChickenVariantComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.ChickenVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class ChickenVariantComponentImpl implements ChickenVariantComponent {

    String registryKey;
    ChickenVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        if(!byteBuf.readBoolean()){
            registryKey = ProtocolUtil.readString(byteBuf);
        } else {
            variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, ChickenVariant.class);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        byteBuf.writeBoolean(registryKey == null);
        if(registryKey != null){
            ProtocolUtil.writeString(byteBuf, registryKey);
        } else {
            DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<ChickenVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:chicken_variant";
        }

        @Override
        public ChickenVariantComponent createEmpty() {
            return new ChickenVariantComponentImpl(null, null);
        }

        @Override
        public ChickenVariantComponent create(String registryKey) {
            return new ChickenVariantComponentImpl(registryKey, null);
        }

        @Override
        public ChickenVariantComponent create(ChickenVariant variant) {
            return new ChickenVariantComponentImpl(null, variant);
        }
    }

}
