package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DeathProtectionComponent;
import dev.simplix.protocolize.api.item.component.DataComponentType;
import dev.simplix.protocolize.api.item.objects.ConsumeEffect;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.DataComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DeathProtectionComponentImpl implements DeathProtectionComponent {

    private List<ConsumeEffect.ConsumeEffectInstance> deathEffects;

    @Override
    public void addDeathEffect(ConsumeEffect.ConsumeEffectInstance consumeEffectInstance) {
        deathEffects.add(consumeEffectInstance);
    }

    @Override
    public void removeDeathEffect(ConsumeEffect.ConsumeEffectInstance consumeEffectInstance) {
        deathEffects.remove(consumeEffectInstance);
    }

    @Override
    public void clearDeathEffects() {
        deathEffects.clear();
    }

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        deathEffects = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            deathEffects.add(DataComponentUtil.readConsumeEffect(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, deathEffects.size());
        for(ConsumeEffect.ConsumeEffectInstance consumeEffectInstance : deathEffects) {
            DataComponentUtil.writeConsumeEffect(byteBuf, protocolVersion, consumeEffectInstance);
        }
    }

    @Override
    public DataComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements DataComponentType<DeathProtectionComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public DeathProtectionComponent create(List<ConsumeEffect.ConsumeEffectInstance> types) {
            return new DeathProtectionComponentImpl(types);
        }

        @Override
        public String getName() {
            return "minecraft:death_protection";
        }

        @Override
        public DeathProtectionComponent createEmpty() {
            return new DeathProtectionComponentImpl(new ArrayList<>());
        }

    }
}
