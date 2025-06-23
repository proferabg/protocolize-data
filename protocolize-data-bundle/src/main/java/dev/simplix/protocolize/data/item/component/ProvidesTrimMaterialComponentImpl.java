package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.component.ProvidesTrimMaterialComponent;
import dev.simplix.protocolize.api.item.objects.DirectTrimMaterial;
import dev.simplix.protocolize.api.item.objects.Holder;
import dev.simplix.protocolize.data.TrimMaterial;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class ProvidesTrimMaterialComponentImpl implements ProvidesTrimMaterialComponent {

    Holder<TrimMaterial, DirectTrimMaterial> material;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        material = DataComponentUtil.readHolder(byteBuf, protocolVersion, TrimMaterial.class, DataComponentUtil::readTrimMaterial);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        DataComponentUtil.writeHolder(byteBuf, protocolVersion, material, TrimMaterial.class, DataComponentUtil::writeTrimMaterial);
    }

    public void setMaterial(Holder<TrimMaterial, DirectTrimMaterial> holder) {
        material = holder;
    }

    public void setMaterial(DirectTrimMaterial directTrimMaterial) {
        material = Holder.direct(directTrimMaterial);
    }

    public void setMaterial(TrimMaterial trimMaterial) {
        material = Holder.registry(trimMaterial);
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<ProvidesTrimMaterialComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:provides_trim_material";
        }

        @Override
        public ProvidesTrimMaterialComponent createEmpty() {
            return new ProvidesTrimMaterialComponentImpl(null);
        }

        @Override
        public ProvidesTrimMaterialComponent create(Holder<TrimMaterial, DirectTrimMaterial> material) {
            return new ProvidesTrimMaterialComponentImpl(material);
        }

        @Override
        public ProvidesTrimMaterialComponent create(DirectTrimMaterial material) {
            return new ProvidesTrimMaterialComponentImpl(Holder.direct(material));
        }

        @Override
        public ProvidesTrimMaterialComponent create(TrimMaterial trimMaterial) {
            return new ProvidesTrimMaterialComponentImpl(Holder.registry(trimMaterial));
        }
    }

}
