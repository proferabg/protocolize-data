package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.LlamaVariantComponent;
import dev.simplix.protocolize.data.LlamaVariant;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class LlamaVariantComponentImpl implements LlamaVariantComponent {

    LlamaVariant variant;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        variant = DataComponentUtil.readRegistry(byteBuf, protocolVersion, LlamaVariant.class);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeRegistry(byteBuf, protocolVersion, variant);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<LlamaVariantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:llama_variant";
        }

        @Override
        public LlamaVariantComponent createEmpty() {
            return new LlamaVariantComponentImpl(null);
        }

        @Override
        public LlamaVariantComponent create(LlamaVariant variant) {
            return new LlamaVariantComponentImpl(variant);
        }
    }

}
