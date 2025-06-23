package dev.simplix.protocolize.data.util;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.objects.BlockPredicate;
import dev.simplix.protocolize.api.item.objects.ConsumeEffect;
import dev.simplix.protocolize.api.item.objects.DamageReduction;
import dev.simplix.protocolize.api.item.objects.DirectBannerPattern;
import dev.simplix.protocolize.api.item.objects.DirectTrimMaterial;
import dev.simplix.protocolize.api.item.objects.DirectTrimPattern;
import dev.simplix.protocolize.api.item.objects.Firework;
import dev.simplix.protocolize.api.item.objects.Holder;
import dev.simplix.protocolize.api.item.objects.HolderSet;
import dev.simplix.protocolize.api.item.objects.ItemDamageFunction;
import dev.simplix.protocolize.api.item.objects.MaterialAssetGroup;
import dev.simplix.protocolize.api.item.objects.MobEffectInstance;
import dev.simplix.protocolize.api.item.objects.SoundEvent;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolStringMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.api.util.TriFunction;
import dev.simplix.protocolize.data.ArmorMaterial;
import dev.simplix.protocolize.data.Block;
import dev.simplix.protocolize.data.ConsumeEffectType;
import dev.simplix.protocolize.data.DamageType;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.MobEffect;
import dev.simplix.protocolize.data.Sound;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j(topic = "Protocolize")
public class DataComponentUtil {

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    public static BlockPredicate.Property readProperty(ByteBuf byteBuf, int protocolVersion){
        BlockPredicate.Property property = new BlockPredicate.Property();
        property.setName(ProtocolUtil.readString(byteBuf));
        if(byteBuf.readBoolean()){
            property.setExactValue(ProtocolUtil.readString(byteBuf));
        } else {
            property.setMinValue(ProtocolUtil.readString(byteBuf));
            property.setMaxValue(ProtocolUtil.readString(byteBuf));
        }
        return property;
    }

    public static void writeProperty(ByteBuf byteBuf, int protocolVersion, BlockPredicate.Property property){
        ProtocolUtil.writeString(byteBuf, property.getName());
        byteBuf.writeBoolean(property.getExactValue() != null);
        if(property.getExactValue() != null){
            ProtocolUtil.writeString(byteBuf, property.getExactValue());
        } else {
            ProtocolUtil.writeString(byteBuf, property.getMinValue());
            ProtocolUtil.writeString(byteBuf, property.getMaxValue());
        }
    }

    public static <T extends Enum<T>> T readRegistry(ByteBuf byteBuf, int protocolVersion, Class<T> clazz){
        return MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, clazz);
    }

    public static <T extends Enum<T>> void writeRegistry(ByteBuf byteBuf, int protocolVersion, T t){
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(t, protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            DataComponentUtil.logMappingWarning(t.name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
        } else {
            ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
        }
    }

    public static Firework.Meta readFireworkMeta(ByteBuf byteBuf, int protocolVersion){
        Firework.Meta meta = new Firework.Meta();
        meta.setShape(Firework.Meta.Shape.values()[ProtocolUtil.readVarInt(byteBuf)]);
        int count = ProtocolUtil.readVarInt(byteBuf);
        List<Integer> colors = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            colors.add(byteBuf.readInt());
        }
        meta.setColors(colors);
        int fadeCount = ProtocolUtil.readVarInt(byteBuf);
        List<Integer> fadeColors = new ArrayList<>(fadeCount);
        for(int i = 0; i < fadeCount; i++){
            fadeColors.add(byteBuf.readInt());
        }
        meta.setFadeColors(fadeColors);
        meta.setTrail(byteBuf.readBoolean());
        meta.setTwinkle(byteBuf.readBoolean());
        return meta;
    }

    public static void writeFireworkMeta(ByteBuf byteBuf, int protocolVersion, Firework.Meta meta) {
        ProtocolUtil.writeVarInt(byteBuf, meta.getShape().ordinal());
        ProtocolUtil.writeVarInt(byteBuf, meta.getColors().size());
        for(int color : meta.getColors()){
            byteBuf.writeInt(color);
        }
        ProtocolUtil.writeVarInt(byteBuf, meta.getFadeColors().size());
        for(int color : meta.getFadeColors()){
            byteBuf.writeInt(color);
        }
        byteBuf.writeBoolean(meta.isTrail());
        byteBuf.writeBoolean(meta.isTwinkle());
    }

    public static SoundEvent readSoundEvent(ByteBuf byteBuf, int protocolVersion) {
        SoundEvent soundEvent = new SoundEvent();
        int type = ProtocolUtil.readVarInt(byteBuf);
        if(type != 0) {
            soundEvent.setSound(MAPPING_PROVIDER.mapIdToEnum(type - 1, protocolVersion, Sound.class));
        } else {
            soundEvent.setIdentifier(ProtocolUtil.readString(byteBuf));
            if(byteBuf.readBoolean()){
                soundEvent.setFixedRange(byteBuf.readFloat());
            }
        }
        return soundEvent;
    }

    public static void writeSoundEvent(ByteBuf byteBuf, int protocolVersion, SoundEvent soundEvent) {
        if(soundEvent.getSound() != null){
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(soundEvent.getSound(), protocolVersion);
            if (!(mapping instanceof ProtocolStringMapping)) {
                logMappingWarning(soundEvent.getSound().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 1);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolStringMapping) mapping).protocolId() + 1);
            }
        } else {
            ProtocolUtil.writeString(byteBuf, soundEvent.getIdentifier());
            byteBuf.writeBoolean(soundEvent.getFixedRange() != null);
            if(soundEvent.getFixedRange() != null){
                byteBuf.writeFloat(soundEvent.getFixedRange());
            }
        }
    }

    public static BlockPredicate readBlockPredicate(ByteBuf byteBuf, int protocolVersion) throws IOException {
        BlockPredicate blockPredicate = new BlockPredicate();
        if(byteBuf.readBoolean()){
            blockPredicate.setBlockSet(DataComponentUtil.readHolderSet(byteBuf, protocolVersion, Block.class));
        }
        if(byteBuf.readBoolean()){
            int propertyCount = ProtocolUtil.readVarInt(byteBuf);
            List<BlockPredicate.Property> properties = new ArrayList<>(propertyCount);
            for(int j = 0; j < propertyCount; j++){
                properties.add(DataComponentUtil.readProperty(byteBuf, protocolVersion));
            }
            blockPredicate.setProperties(properties);
        }
        if(byteBuf.readBoolean()){
            blockPredicate.setNbtData((CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
        }
        return blockPredicate;
    }

    public static void writeBlockPredicate(ByteBuf byteBuf, int protocolVersion, BlockPredicate blockPredicate) throws IOException {
        boolean hasBlockSet = blockPredicate.getBlockSet() != null;
        byteBuf.writeBoolean(hasBlockSet);
        if(hasBlockSet){
            DataComponentUtil.writeHolderSet(byteBuf, protocolVersion, blockPredicate.getBlockSet(), Block.class);
        }
        boolean hasProperties = blockPredicate.getProperties() != null && !blockPredicate.getProperties().isEmpty();
        byteBuf.writeBoolean(hasProperties);
        if(hasProperties){
            for(BlockPredicate.Property property : blockPredicate.getProperties()){
                DataComponentUtil.writeProperty(byteBuf, protocolVersion, property);
            }
        }
        boolean hasNbtData = blockPredicate.getNbtData() != null;
        byteBuf.writeBoolean(hasNbtData);
        if(hasNbtData){
            NamedBinaryTagUtil.writeTag(byteBuf, blockPredicate.getNbtData(), protocolVersion);
        }
    }

    public static MobEffectInstance readMobEffectInstance(ByteBuf byteBuf, int protocolVersion) {
        MobEffect mobEffect = readRegistry(byteBuf, protocolVersion, MobEffect.class);
        MobEffectInstance.Details details = DataComponentUtil.readMobEffectDetails(byteBuf, protocolVersion);
        return new MobEffectInstance(mobEffect, details);
    }

    public static void writeMobEffectInstance(ByteBuf byteBuf, int protocolVersion, MobEffectInstance mobEffectInstance) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(mobEffectInstance.getMobEffect(), protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            logMappingWarning(mobEffectInstance.getMobEffect().name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
            DataComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, new MobEffectInstance.Details(0, 0, false, false, false, null));
            return;
        }
        ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
        DataComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, mobEffectInstance.getDetails());
    }

    public static MobEffectInstance.Details readMobEffectDetails(ByteBuf byteBuf, int protocolVersion) {
        int amplifier = ProtocolUtil.readVarInt(byteBuf);
        int duration = ProtocolUtil.readVarInt(byteBuf);
        boolean ambient = byteBuf.readBoolean();
        boolean showParticles = byteBuf.readBoolean();
        boolean showIcon = byteBuf.readBoolean();
        MobEffectInstance.Details hiddenEffect = byteBuf.readBoolean() ? readMobEffectDetails(byteBuf, protocolVersion) : null;
        return new MobEffectInstance.Details(amplifier, duration, ambient, showParticles, showIcon, hiddenEffect);
    }

    public static void writeMobEffectDetails(ByteBuf byteBuf, int protocolVersion, MobEffectInstance.Details effect) {
        ProtocolUtil.writeVarInt(byteBuf, effect.getAmplifier());
        ProtocolUtil.writeVarInt(byteBuf, effect.getDuration());
        byteBuf.writeBoolean(effect.isAmbient());
        byteBuf.writeBoolean(effect.isShowParticles());
        byteBuf.writeBoolean(effect.isShowIcon());
        byteBuf.writeBoolean(effect.getHiddenEffect() != null);
        if(effect.getHiddenEffect() != null){
            writeMobEffectDetails(byteBuf, protocolVersion, effect.getHiddenEffect());
        }
    }

    public static boolean writeItemMapping(ByteBuf byteBuf, int protocolVersion, ItemType itemType) {
        return writeItemMapping(byteBuf, itemType, protocolVersion, 0);
    }

    public static boolean writeItemMapping(ByteBuf byteBuf, ItemType itemType, int protocolVersion, int offset) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(itemType, protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            logMappingWarning(itemType.name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
            return false;
        }
        ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id() + offset);
        return true;
    }

    public static ConsumeEffect.ConsumeEffectInstance readConsumeEffect(ByteBuf byteBuf, int protocolVersion) {
        ConsumeEffectType type = readRegistry(byteBuf, protocolVersion, ConsumeEffectType.class);

        switch (type.name()) {
            case "APPLY_EFFECTS": {
                int count = ProtocolUtil.readVarInt(byteBuf);
                List<MobEffectInstance> effects = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    effects.add(readMobEffectInstance(byteBuf, protocolVersion));
                }
                float probability = byteBuf.readFloat();
                return new ConsumeEffect.ApplyStatusEffects(effects, probability);
            }
            case "REMOVE_EFFECTS": {
                return new ConsumeEffect.RemoveStatusEffects(readHolderSet(byteBuf, protocolVersion, MobEffect.class));
            }
            case "CLEAR_ALL_EFFECTS":
                return new ConsumeEffect.ClearAllStatusEffects();
            case "TELEPORT_RANDOMLY":
                float diameter = byteBuf.readFloat();
                return new ConsumeEffect.TeleportRandomly(diameter);
            case "PLAY_SOUND":
                SoundEvent sound = readSoundEvent(byteBuf, protocolVersion);
                return new ConsumeEffect.PlaySound(sound);
            default:
                throw new IllegalArgumentException("Invalid ConsumeEffectType " + type.name());
        }
    }

    public static void writeConsumeEffect(ByteBuf byteBuf, int protocolVersion, ConsumeEffect.ConsumeEffectInstance effectInstance){
        ProtocolMapping consumeEffectTypeMapping = MAPPING_PROVIDER.mapping(effectInstance.getType(), protocolVersion);
        if (!(consumeEffectTypeMapping instanceof ProtocolIdMapping)) {
            logMappingWarning(effectInstance.getType().name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
            return;
        }
        ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) consumeEffectTypeMapping).id());
        if(effectInstance instanceof ConsumeEffect.ApplyStatusEffects){
            ConsumeEffect.ApplyStatusEffects applyStatusEffects = (ConsumeEffect.ApplyStatusEffects) effectInstance;
            ProtocolUtil.writeVarInt(byteBuf, applyStatusEffects.getEffects().size());
            for(MobEffectInstance effect : applyStatusEffects.getEffects()){
                writeMobEffectInstance(byteBuf, protocolVersion, effect);
            }
            byteBuf.writeFloat(applyStatusEffects.getProbability());
        } else if (effectInstance instanceof ConsumeEffect.RemoveStatusEffects) {
            ConsumeEffect.RemoveStatusEffects removeStatusEffects = (ConsumeEffect.RemoveStatusEffects) effectInstance;
            writeHolderSet(byteBuf, protocolVersion, removeStatusEffects.getEffects(), MobEffect.class);
        } else if (effectInstance instanceof ConsumeEffect.ClearAllStatusEffects) {
            ConsumeEffect.ClearAllStatusEffects clearAllStatusEffects = (ConsumeEffect.ClearAllStatusEffects) effectInstance;
            // do nothing here
        } else if (effectInstance instanceof ConsumeEffect.TeleportRandomly) {
            ConsumeEffect.TeleportRandomly teleportRandomly = (ConsumeEffect.TeleportRandomly) effectInstance;
            byteBuf.writeFloat(teleportRandomly.getDiameter());
        } else if (effectInstance instanceof ConsumeEffect.PlaySound) {
            ConsumeEffect.PlaySound playSound = (ConsumeEffect.PlaySound) effectInstance;
            writeSoundEvent(byteBuf, protocolVersion, playSound.getSound());
        }
    }

    public static <T extends Enum<T>> HolderSet<T> readHolderSet(ByteBuf byteBuf, int protocolVersion, Class<T> clazz) {
        int count = ProtocolUtil.readVarInt(byteBuf) - 1;
        if(count == -1){
            return new HolderSet<>(ProtocolUtil.readString(byteBuf));
        } else {
            List<T> list = new ArrayList<>(count);
            for(int i = 0; i < count; i++){
                list.add(readRegistry(byteBuf, protocolVersion, clazz));
            }
            return new HolderSet<>(list);
        }
    }

    public static <T extends Enum<T>> void writeHolderSet(ByteBuf byteBuf, int protocolVersion, HolderSet<T> holderSet, Class<T> clazz) {
        ProtocolUtil.writeVarInt(byteBuf, holderSet.hasSet() ? holderSet.getSet().size() + 1 : 0);
        if(holderSet.hasSet()){
            for(T item : holderSet.getSet()){
                ProtocolMapping mapping = MAPPING_PROVIDER.mapping(item, protocolVersion);
                if (!(mapping instanceof ProtocolIdMapping)) {
                    logMappingWarning(item.name(), protocolVersion);
                    ProtocolUtil.writeVarInt(byteBuf, 0);
                    return;
                }
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
        } else {
            ProtocolUtil.writeString(byteBuf, holderSet.getTag());
        }
    }

    public static ItemDamageFunction readItemDamageFunction(ByteBuf byteBuf, int protocolVersion) {
        ItemDamageFunction itemDamageFunction = new ItemDamageFunction();
        itemDamageFunction.setThreshold(byteBuf.readFloat());
        itemDamageFunction.setBase(byteBuf.readFloat());
        itemDamageFunction.setFactor(byteBuf.readFloat());
        return itemDamageFunction;
    }

    public static void writeItemDamageFunction(ByteBuf byteBuf, int protocolVersion, ItemDamageFunction itemDamageFunction) {
        byteBuf.writeFloat(itemDamageFunction.getThreshold());
        byteBuf.writeFloat(itemDamageFunction.getBase());
        byteBuf.writeFloat(itemDamageFunction.getFactor());
    }

    public static DamageReduction readDamageReduction(ByteBuf byteBuf, int protocolVersion) {
        DamageReduction damageReduction = new DamageReduction();
        damageReduction.setHorizontalBlockingAngle(byteBuf.readFloat());
        if(byteBuf.readBoolean()){
            damageReduction.setType(DataComponentUtil.readHolderSet(byteBuf, protocolVersion, DamageType.class));
        }
        damageReduction.setBase(byteBuf.readFloat());
        damageReduction.setFactor(byteBuf.readFloat());
        return damageReduction;
    }

    public static void writeDamageReduction(ByteBuf byteBuf, int protocolVersion, DamageReduction damageReduction) {
        byteBuf.writeFloat(damageReduction.getHorizontalBlockingAngle());
        boolean hasDamageType = damageReduction.getType() != null;
        byteBuf.writeBoolean(hasDamageType);
        if(hasDamageType){
            DataComponentUtil.writeHolderSet(byteBuf, protocolVersion, damageReduction.getType(), DamageType.class);
        }
        byteBuf.writeFloat(damageReduction.getBase());
        byteBuf.writeFloat(damageReduction.getFactor());
    }

    public static MaterialAssetGroup readMaterialAssetGroup(ByteBuf byteBuf, int protocolVersion) {
        MaterialAssetGroup materialAssetGroup = new MaterialAssetGroup();
        materialAssetGroup.setBase(ProtocolUtil.readString(byteBuf));
        int count = ProtocolUtil.readVarInt(byteBuf);
        Map<String, String> overrides = new HashMap<>(count);
        for(int i = 0; i < count; i++){
            overrides.put(ProtocolUtil.readString(byteBuf), ProtocolUtil.readString(byteBuf));
        }
        materialAssetGroup.setOverrides(overrides);
        return materialAssetGroup;
    }

    public static void writeMaterialAssetGroup(ByteBuf byteBuf, int protocolVersion, MaterialAssetGroup materialAssetGroup) {
        ProtocolUtil.writeString(byteBuf, materialAssetGroup.getBase());
        ProtocolUtil.writeVarInt(byteBuf, materialAssetGroup.getOverrides().size());
        for(Map.Entry<String, String> override : materialAssetGroup.getOverrides().entrySet()){
            ProtocolUtil.writeString(byteBuf, override.getKey());
            ProtocolUtil.writeString(byteBuf, override.getValue());
        }
    }

    public static DirectTrimMaterial readTrimMaterial(ByteBuf byteBuf, int protocolVersion) {
        try {
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5){
                DirectTrimMaterial.DirectTrimMaterial_v770 trimMaterial = new DirectTrimMaterial.DirectTrimMaterial_v770();
                trimMaterial.setMaterialAssetGroup(readMaterialAssetGroup(byteBuf, protocolVersion));
                trimMaterial.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                return trimMaterial;
            }
            else if(protocolVersion == ProtocolVersions.MINECRAFT_1_21_4){
                DirectTrimMaterial.DirectTrimMaterial_v769 trimMaterial = new DirectTrimMaterial.DirectTrimMaterial_v769();
                trimMaterial.setAssetName(ProtocolUtil.readString(byteBuf));
                trimMaterial.setIngredient(readRegistry(byteBuf, protocolVersion, ItemType.class));
                int count = ProtocolUtil.readVarInt(byteBuf);
                Map<String, String> overrides = new HashMap<>(count);
                for(int i = 0; i < count; i++){
                    overrides.put(ProtocolUtil.readString(byteBuf), ProtocolUtil.readString(byteBuf));
                }
                trimMaterial.setOverrides(overrides);
                trimMaterial.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                return trimMaterial;
            }
            else if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_2){
                DirectTrimMaterial.DirectTrimMaterial_v768 trimMaterial = new DirectTrimMaterial.DirectTrimMaterial_v768();
                trimMaterial.setAssetName(ProtocolUtil.readString(byteBuf));
                trimMaterial.setIngredient(readRegistry(byteBuf, protocolVersion, ItemType.class));
                trimMaterial.setItemModelIndex(byteBuf.readFloat());
                int count = ProtocolUtil.readVarInt(byteBuf);
                Map<String, String> overrides = new HashMap<>(count);
                for(int i = 0; i < count; i++){
                    overrides.put(ProtocolUtil.readString(byteBuf), ProtocolUtil.readString(byteBuf));
                }
                trimMaterial.setOverrides(overrides);
                trimMaterial.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                return trimMaterial;
            }
            else {
                DirectTrimMaterial.DirectTrimMaterial_v766 trimMaterial = new DirectTrimMaterial.DirectTrimMaterial_v766();
                trimMaterial.setAssetName(ProtocolUtil.readString(byteBuf));
                trimMaterial.setIngredient(readRegistry(byteBuf, protocolVersion, ItemType.class));
                trimMaterial.setItemModelIndex(byteBuf.readFloat());
                int count = ProtocolUtil.readVarInt(byteBuf);
                Map<ArmorMaterial, String> overrides = new HashMap<>(count);
                for(int i = 0; i < count; i++){
                    overrides.put(readRegistry(byteBuf, protocolVersion, ArmorMaterial.class), ProtocolUtil.readString(byteBuf));
                }
                trimMaterial.setOverrides(overrides);
                trimMaterial.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                return trimMaterial;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read TrimMaterial.", e);
        }
    }

    public static Void writeTrimMaterial(ByteBuf byteBuf, int protocolVersion, DirectTrimMaterial directTrimMaterial) {
        try {
            if (directTrimMaterial instanceof DirectTrimMaterial.DirectTrimMaterial_v770) {
                DirectTrimMaterial.DirectTrimMaterial_v770 trimMaterial = (DirectTrimMaterial.DirectTrimMaterial_v770) directTrimMaterial;
            } else if (directTrimMaterial instanceof DirectTrimMaterial.DirectTrimMaterial_v769) {
                DirectTrimMaterial.DirectTrimMaterial_v769 trimMaterial = (DirectTrimMaterial.DirectTrimMaterial_v769) directTrimMaterial;
                ProtocolUtil.writeString(byteBuf, trimMaterial.getAssetName());
                DataComponentUtil.writeItemMapping(byteBuf, protocolVersion, trimMaterial.getIngredient());
                ProtocolUtil.writeVarInt(byteBuf, trimMaterial.getOverrides().size());
                for (Map.Entry<String, String> entry : trimMaterial.getOverrides().entrySet()) {
                    ProtocolUtil.writeString(byteBuf, (String) entry.getKey());
                    ProtocolUtil.writeString(byteBuf, entry.getValue());
                }
                NamedBinaryTagUtil.writeTag(byteBuf, trimMaterial.getDescription().asNbt(), protocolVersion);
            } else if (directTrimMaterial instanceof DirectTrimMaterial.DirectTrimMaterial_v768) {
                DirectTrimMaterial.DirectTrimMaterial_v768 trimMaterial = (DirectTrimMaterial.DirectTrimMaterial_v768) directTrimMaterial;
                ProtocolUtil.writeString(byteBuf, trimMaterial.getAssetName());
                DataComponentUtil.writeItemMapping(byteBuf, protocolVersion, trimMaterial.getIngredient());
                byteBuf.writeFloat(trimMaterial.getItemModelIndex());
                ProtocolUtil.writeVarInt(byteBuf, trimMaterial.getOverrides().size());
                for (Map.Entry<String, String> entry : trimMaterial.getOverrides().entrySet()) {
                    ProtocolUtil.writeString(byteBuf, (String) entry.getKey());
                    ProtocolUtil.writeString(byteBuf, entry.getValue());
                }
                NamedBinaryTagUtil.writeTag(byteBuf, trimMaterial.getDescription().asNbt(), protocolVersion);
            } else {
                DirectTrimMaterial.DirectTrimMaterial_v766 trimMaterial = (DirectTrimMaterial.DirectTrimMaterial_v766) directTrimMaterial;
                ProtocolUtil.writeString(byteBuf, trimMaterial.getAssetName());
                DataComponentUtil.writeItemMapping(byteBuf, protocolVersion, trimMaterial.getIngredient());
                byteBuf.writeFloat(trimMaterial.getItemModelIndex());
                ProtocolUtil.writeVarInt(byteBuf, trimMaterial.getOverrides().size());
                for (Map.Entry<ArmorMaterial, String> entry : trimMaterial.getOverrides().entrySet()) {
                    writeRegistry(byteBuf, protocolVersion, entry.getKey());
                    ProtocolUtil.writeString(byteBuf, entry.getValue());
                }
                NamedBinaryTagUtil.writeTag(byteBuf, trimMaterial.getDescription().asNbt(), protocolVersion);
            }
            return null; // Void != void
        } catch (Exception e){
            throw new RuntimeException("Failed to write TrimMaterial.", e);
        }
    }

    public static DirectTrimPattern readTrimPattern(ByteBuf byteBuf, int protocolVersion) {
        try {
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5) {
                DirectTrimPattern.DirectTrimPattern_v770 trimPattern = new DirectTrimPattern.DirectTrimPattern_v770();
                trimPattern.setAssetId(ProtocolUtil.readString(byteBuf));
                trimPattern.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                trimPattern.setDecal(byteBuf.readBoolean());
                return trimPattern;
            } else {
                DirectTrimPattern.DirectTrimPattern_v766 trimPattern = new DirectTrimPattern.DirectTrimPattern_v766();
                trimPattern.setAssetId(ProtocolUtil.readString(byteBuf));
                trimPattern.setTemplateItem(readRegistry(byteBuf, protocolVersion, ItemType.class));
                trimPattern.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                trimPattern.setDecal(byteBuf.readBoolean());
                return trimPattern;
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to read TrimPattern.", e);
        }
    }

    public static Void writeTrimPattern(ByteBuf byteBuf, int protocolVersion, DirectTrimPattern directTrimPattern) {
        try {
            if (directTrimPattern instanceof DirectTrimPattern.DirectTrimPattern_v770) {
                DirectTrimPattern.DirectTrimPattern_v770 trimPattern = (DirectTrimPattern.DirectTrimPattern_v770) directTrimPattern;
                ProtocolUtil.writeString(byteBuf, trimPattern.getAssetId());
                NamedBinaryTagUtil.writeTag(byteBuf, trimPattern.getDescription().asNbt(), protocolVersion);
                byteBuf.writeBoolean(trimPattern.isDecal());
            } else {
                DirectTrimPattern.DirectTrimPattern_v766 trimPattern = (DirectTrimPattern.DirectTrimPattern_v766) directTrimPattern;
                ProtocolUtil.writeString(byteBuf, trimPattern.getAssetId());
                DataComponentUtil.writeItemMapping(byteBuf, protocolVersion, trimPattern.getTemplateItem());
                NamedBinaryTagUtil.writeTag(byteBuf, trimPattern.getDescription().asNbt(), protocolVersion);
                byteBuf.writeBoolean(trimPattern.isDecal());
            }
            return null;
        } catch (Exception e){
            throw new RuntimeException("Failed to write TrimPattern.", e);
        }
    }

    public static DirectBannerPattern readBannerPattern(ByteBuf byteBuf, int protocolVersion) {
        DirectBannerPattern directBannerPattern = new DirectBannerPattern();
        directBannerPattern.setAssetId(ProtocolUtil.readString(byteBuf));
        directBannerPattern.setTranslationKey(ProtocolUtil.readString(byteBuf));
        return directBannerPattern;
    }

    public static Void writeBannerPattern(ByteBuf byteBuf, int protocolVersion, DirectBannerPattern directBannerPattern) {
        ProtocolUtil.writeString(byteBuf, directBannerPattern.getAssetId());
        ProtocolUtil.writeString(byteBuf, directBannerPattern.getTranslationKey());
        return null;
    }

    public static <T extends Enum<T>, U> Holder<T, U> readHolder(ByteBuf byteBuf, int protocolVersion, Class<T> clazz, BiFunction<ByteBuf, Integer, U> directReader) {
        int count = ProtocolUtil.readVarInt(byteBuf);
        if(count == 0){
            return Holder.direct(directReader.apply(byteBuf, protocolVersion));
        } else {
            return Holder.registry(MAPPING_PROVIDER.mapIdToEnum(count - 1, protocolVersion, clazz));
        }
    }

    public static <T extends Enum<T>, U> void writeHolder(ByteBuf byteBuf, int protocolVersion, Holder<T, U> holderSet, Class<T> clazz, TriFunction<ByteBuf, Integer, U, Void> directWriter) {
        if(holderSet.isDirect()){
            ProtocolUtil.writeVarInt(byteBuf, 0);
            directWriter.apply(byteBuf, protocolVersion, holderSet.getDirect());
        } else {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(holderSet.getRegistry(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                logMappingWarning(holderSet.getRegistry().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 1);
                return;
            }
            ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id() + 1);
        }
    }

    public static void logMappingWarning(String name, int protocolVersion){
        log.warn("{} cannot be used on protocol version {}", name, protocolVersion);
    }
}
