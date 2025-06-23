package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.StoredEnchantmentsComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.EnchantmentHolder;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.Enchantment;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class StoredEnchantmentsComponentImpl implements StoredEnchantmentsComponent {

    private Map<EnchantmentHolder<Integer>, Integer> enchantments;
    private boolean showInTooltip;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        enchantments = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            int enchantmentId = ProtocolUtil.readVarInt(byteBuf);
            Enchantment enchantment = MAPPING_PROVIDER.mapIdToEnum(enchantmentId, protocolVersion, Enchantment.class);
            int level = ProtocolUtil.readVarInt(byteBuf);
            enchantments.put(enchantment != null ? new EnchantmentHolder<>(enchantment) : new EnchantmentHolder<>(enchantmentId), level);
        }
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            showInTooltip = byteBuf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, enchantments.size());
        for (Map.Entry<EnchantmentHolder<Integer>, Integer> entry : enchantments.entrySet()) {
            if(entry.getKey().isRegistryType()) {
                ProtocolMapping mapping = MAPPING_PROVIDER.mapping(entry.getKey().getRegistryType(), protocolVersion);
                if (!(mapping instanceof ProtocolIdMapping)) {
                    DataComponentUtil.logMappingWarning(entry.getKey().getRegistryType().name(), protocolVersion);
                    ProtocolUtil.writeVarInt(byteBuf, 0);
                } else {
                    ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
                }
            } else {
                ProtocolUtil.writeVarInt(byteBuf, entry.getKey().getCastType());
            }
            ProtocolUtil.writeVarInt(byteBuf, entry.getValue());
        }
        if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_4) {
            byteBuf.writeBoolean(showInTooltip);
        }
    }

    @Override
    public void removeEnchantment(Enchantment enchantment) {
        enchantments.entrySet().removeIf(container -> container.getKey().isRegistryType() && enchantment.equals(container.getKey().getRegistryType()));
    }

    @Override
    public void removeEnchantment(int enchantmentId) {
        enchantments.entrySet().removeIf(container -> container.getKey().isRawType() && container.getKey().getCastType() == enchantmentId);
    }

    @Override
    public void addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(new EnchantmentHolder<>(enchantment), level);
    }

    @Override
    public void addEnchantment(int enchantmentId, int level) {
        enchantments.put(new EnchantmentHolder<>(enchantmentId), level);
    }

    @Override
    public void removeAllEnchantments() {
        enchantments.clear();
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<StoredEnchantmentsComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public StoredEnchantmentsComponent create(Map<EnchantmentHolder<Integer>, Integer> enchantments) {
            return new StoredEnchantmentsComponentImpl(enchantments, true);
        }

        @Override
        public StoredEnchantmentsComponent create(Map<EnchantmentHolder<Integer>, Integer> enchantments, boolean showInTooltip) {
            return new StoredEnchantmentsComponentImpl(enchantments, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:stored_enchantments";
        }

        @Override
        public StoredEnchantmentsComponent createEmpty() {
            return new StoredEnchantmentsComponentImpl(new HashMap<>(0), true);
        }

    }

}
