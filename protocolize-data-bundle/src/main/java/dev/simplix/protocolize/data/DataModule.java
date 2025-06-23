package dev.simplix.protocolize.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.*;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.item.component.*;
import dev.simplix.protocolize.data.listeners.*;
import dev.simplix.protocolize.data.packets.*;
import dev.simplix.protocolize.data.registries.Registries;
import dev.simplix.protocolize.data.registries.RegistryEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
public class DataModule implements ProtocolizeModule {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public void registerMappings(MappingProvider mappingProvider) {
        for (int i = ProtocolVersions.MINECRAFT_1_13; i <= ProtocolVersions.MINECRAFT_LATEST; i++) {
            registerMappingsForProtocol(mappingProvider, i);
        }
    }

    private void registerMappingsForProtocol(MappingProvider provider, int protocolVersion) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + protocolVersion + "/registries.json")) {
            if (stream == null) {
                registerItemsUsingLegacyFormat(provider, protocolVersion);
                return;
            }
            Registries registries = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Registries.class);
            registerIdMappings(registries.itemRegistry().entries(), provider, protocolVersion, ItemType.class);
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_20_5) {
                registerIdMappings(registries.attributeRegistry().entries(), provider, protocolVersion, Attribute.class);
                registerIdMappings(registries.bannerPatternRegistry().entries(), provider, protocolVersion, BannerPattern.class);
                registerIdMappings(registries.blockRegistry().entries(), provider, protocolVersion, Block.class);
                registerIdMappings(registries.damageTypeRegistry().entries(), provider, protocolVersion, DamageType.class);
                registerIdMappings(registries.dataComponentTypeRegistry().entries(), provider, protocolVersion, DataComponentType.class);
                registerIdMappings(registries.enchantmentRegistry().entries(), provider, protocolVersion, Enchantment.class);
                registerIdMappings(registries.instrumentRegistry().entries(), provider, protocolVersion, Instrument.class);
                registerIdMappings(registries.mobEffectRegistry().entries(), provider, protocolVersion, MobEffect.class);
                registerIdMappings(registries.potionRegistry().entries(), provider, protocolVersion, Potion.class);
                registerIdMappings(registries.trimMaterialRegistry().entries(), provider, protocolVersion, TrimMaterial.class);
                registerIdMappings(registries.trimPatternRegistry().entries(), provider, protocolVersion, TrimPattern.class);
                if(protocolVersion <= ProtocolVersions.MINECRAFT_1_21_1) { // 1.20.5 - 1.21.1
                    registerIdMappings(registries.armorMaterialRegistry().entries(), provider, protocolVersion, ArmorMaterial.class);
                }
            }
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_2) {
                registerIdMappings(registries.consumeEffectTypeRegistry().entries(), provider, protocolVersion, ConsumeEffectType.class);
                registerIdMappings(registries.entityTypeRegistry().entries(), provider, protocolVersion, EntityType.class);
            }
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_21_5) {
                registerStringMappings(registries.getDataComponentPredicateTypeRegistry().entries(), provider, protocolVersion, DataComponentPredicateType.class);
                registerIdMappings(registries.axolotlVariantRegistry().entries(), provider, protocolVersion, AxolotlVariant.class);
                registerIdMappings(registries.catVariantRegistry().entries(), provider, protocolVersion, CatVariant.class);
                registerIdMappings(registries.chickenVariantRegistry().entries(), provider, protocolVersion, ChickenVariant.class);
                registerIdMappings(registries.cowVariantRegistry().entries(), provider, protocolVersion, CowVariant.class);
                registerIdMappings(registries.foxVariantRegistry().entries(), provider, protocolVersion, FoxVariant.class);
                registerIdMappings(registries.frogVariantRegistry().entries(), provider, protocolVersion, FrogVariant.class);
                registerIdMappings(registries.horseVariantRegistry().entries(), provider, protocolVersion, HorseVariant.class);
                registerIdMappings(registries.llamaVariantRegistry().entries(), provider, protocolVersion, LlamaVariant.class);
                registerIdMappings(registries.mooshroomVariantRegistry().entries(), provider, protocolVersion, MooshroomVariant.class);
                registerIdMappings(registries.paintingVariantRegistry().entries(), provider, protocolVersion, PaintingVariant.class);
                registerIdMappings(registries.parrotVariantRegistry().entries(), provider, protocolVersion, ParrotVariant.class);
                registerIdMappings(registries.pigVariantRegistry().entries(), provider, protocolVersion, PigVariant.class);
                registerIdMappings(registries.rabbitVariantRegistry().entries(), provider, protocolVersion, RabbitVariant.class);
                registerIdMappings(registries.salmonVariantRegistry().entries(), provider, protocolVersion, SalmonVariant.class);
                registerIdMappings(registries.tropicalFishPatternRegistry().entries(), provider, protocolVersion, TropicalFishPattern.class);
                registerIdMappings(registries.villagerTypeRegistry().entries(), provider, protocolVersion, VillagerType.class);
                registerIdMappings(registries.wolfVariantRegistry().entries(), provider, protocolVersion, WolfVariant.class);
                registerIdMappings(registries.wolfSoundVariantRegistry().entries(), provider, protocolVersion, WolfSoundVariant.class);
            }
            registerStringMappings(registries.soundRegistry().entries(), provider, protocolVersion, Sound.class);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version {}", protocolVersion, e);
        }
    }

    private void registerItemsUsingLegacyFormat(MappingProvider provider, int protocolVersion) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + protocolVersion + "/items.json")) {
            if (stream == null) {
                return;
            }
            Map<String, RegistryEntry> legacy = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), new TypeToken<Map<String, RegistryEntry>>() {
            }.getType());
            registerIdMappings(legacy, provider, protocolVersion, ItemType.class);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version {}", protocolVersion, e);
        }
    }

    private <T extends Enum<T>> void registerIdMappings(Map<String, RegistryEntry> entries, MappingProvider provider, int protocolVersion, Class<T> enumClass) {
        for (String type : entries.keySet()) {
            String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT);
            try {
                T value = Enum.valueOf(enumClass, name);
                int id = entries.get(type).protocolId();
                provider.registerMapping(value, AbstractProtocolMapping.rangedIdMapping(protocolVersion, protocolVersion, id));
            } catch (IllegalArgumentException e) {
                log.warn("Don't know what {}: {} was at protocol {}", enumClass.getSimpleName(), name, protocolVersion);
            }
        }
    }

    private <T extends Enum<T>> void registerStringMappings(Map<String, RegistryEntry> entries, MappingProvider provider, int protocolVersion, Class<T> enumClass) {
        for (String type : entries.keySet()) {
            String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT);
            try {
                T value = Enum.valueOf(enumClass, name);
                int protocolId = entries.get(type).protocolId();
                provider.registerMapping(value, AbstractProtocolMapping.rangedStringMapping(protocolVersion, protocolVersion, type, protocolId));
            } catch (IllegalArgumentException e) {
                log.warn("Don't know what {}: {} was at protocol {}", enumClass.getSimpleName(), name, protocolVersion);
            }
        }
    }

    @Override
    public void registerPackets(ProtocolRegistrationProvider registrationProvider) {
        if (registrationProvider == null) {
            return;
        }
        // CLIENTBOUND
        registrationProvider.registerPacket(WindowItems.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, WindowItems.class);
        registrationProvider.registerPacket(SetSlot.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, SetSlot.class);
        registrationProvider.registerPacket(HeldItemChange.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, HeldItemChange.class);
        registrationProvider.registerPacket(WindowProperty.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, WindowProperty.class);
        registrationProvider.registerPacket(OpenWindow.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, OpenWindow.class);
        registrationProvider.registerPacket(ConfirmTransaction.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, ConfirmTransaction.class);
        registrationProvider.registerPacket(CloseWindow.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, CloseWindow.class);
        registrationProvider.registerPacket(NamedSoundEffect.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, NamedSoundEffect.class);
        registrationProvider.registerPacket(SoundEffect.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, SoundEffect.class);

        // SERVERBOUND
        registrationProvider.registerPacket(UseItem.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, UseItem.class);
        registrationProvider.registerPacket(HeldItemChange.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, HeldItemChange.class);
        registrationProvider.registerPacket(BlockPlacement.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, BlockPlacement.class);
        registrationProvider.registerPacket(ConfirmTransaction.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, ConfirmTransaction.class);
        registrationProvider.registerPacket(ClickWindow.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, ClickWindow.class);
        registrationProvider.registerPacket(CloseWindow.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, CloseWindow.class);
        registrationProvider.registerPacket(PlayerPosition.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerPosition.class);
        registrationProvider.registerPacket(PlayerPositionLook.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerPositionLook.class);
        registrationProvider.registerPacket(PlayerLook.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerLook.class);

        // DATA COMPONENTS
        Protocolize.registerService(ArmorTrimComponent.Factory.class, ArmorTrimComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ArmorTrimComponentImpl.Type.INSTANCE);

        Protocolize.registerService(AttributeModifiersComponent.Factory.class, AttributeModifiersComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(AttributeModifiersComponentImpl.Type.INSTANCE);

        Protocolize.registerService(AxolotlVariantComponent.Factory.class, AxolotlVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(AxolotlVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BannerPatternsComponent.Factory.class, BannerPatternsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BannerPatternsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BaseColorComponent.Factory.class, BaseColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BaseColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BeesComponent.Factory.class, BeesComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BeesComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BlockEntityDataComponent.Factory.class, BlockEntityDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BlockEntityDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BlocksAttacksComponent.Factory.class, BlocksAttacksComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BlocksAttacksComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BlockStateComponent.Factory.class, BlockStateComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BlockStateComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BreakSoundComponent.Factory.class, BreakSoundComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BreakSoundComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BucketEntityDataComponent.Factory.class, BucketEntityDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BucketEntityDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BundleContentsComponent.Factory.class, BundleContentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(BundleContentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CanBreakComponent.Factory.class, CanBreakComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CanBreakComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CanPlaceOnComponent.Factory.class, CanPlaceOnComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CanPlaceOnComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CatCollarComponent.Factory.class, CatCollarComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CatCollarComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CatVariantComponent.Factory.class, CatVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CatVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ChargedProjectilesComponent.Factory.class, ChargedProjectilesComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ChargedProjectilesComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ChickenVariantComponent.Factory.class, ChickenVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ChickenVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ConsumableComponent.Factory.class, ConsumableComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ConsumableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ContainerComponent.Factory.class, ContainerComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ContainerComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ContainerLootComponent.Factory.class, ContainerLootComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ContainerLootComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CowVariantComponent.Factory.class, CowVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CowVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CreativeSlotLockComponent.Factory.class, CreativeSlotLockComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CreativeSlotLockComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CustomDataComponent.Factory.class, CustomDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CustomDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CustomModelDataComponent.Factory.class, CustomModelDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CustomModelDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CustomNameComponent.Factory.class, CustomNameComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(CustomNameComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DamageComponent.Factory.class, DamageComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(DamageComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DamageResistantComponent.Factory.class, DamageResistantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(DamageResistantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DeathProtectionComponent.Factory.class, DeathProtectionComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(DeathProtectionComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DebugStickStateComponent.Factory.class, DebugStickStateComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(DebugStickStateComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DyedColorComponent.Factory.class, DyedColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(DyedColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EnchantableComponent.Factory.class, EnchantableComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(EnchantableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EnchantmentGlintComponent.Factory.class, EnchantmentGlintComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(EnchantmentGlintComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EnchantmentsComponent.Factory.class, EnchantmentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(EnchantmentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EntityDataComponent.Factory.class, EntityDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(EntityDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EquippableComponent.Factory.class, EquippableComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(EquippableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FireResistantComponent.Factory.class, FireResistantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(FireResistantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FireworkExplosionComponent.Factory.class, FireworkExplosionComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(FireworkExplosionComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FireworksComponent.Factory.class, FireworksComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(FireworksComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FoodComponent.Factory.class, FoodComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(FoodComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FoxVariantComponent.Factory.class, FoxVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(FoxVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FrogVariantComponent.Factory.class, FrogVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(FrogVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(GliderComponent.Factory.class, GliderComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(GliderComponentImpl.Type.INSTANCE);

        Protocolize.registerService(HideAdditionalTooltipComponent.Factory.class, HideAdditionalTooltipComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(HideAdditionalTooltipComponentImpl.Type.INSTANCE);

        Protocolize.registerService(HideTooltipComponent.Factory.class, HideTooltipComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(HideTooltipComponentImpl.Type.INSTANCE);

        Protocolize.registerService(HorseVariantComponent.Factory.class, HorseVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(HorseVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(InstrumentComponent.Factory.class, InstrumentComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(InstrumentComponentImpl.Type.INSTANCE);

        Protocolize.registerService(IntangibleProjectileComponent.Factory.class, IntangibleProjectileComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(IntangibleProjectileComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ItemNameComponent.Factory.class, ItemNameComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ItemNameComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ItemModelComponent.Factory.class, ItemModelComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ItemModelComponentImpl.Type.INSTANCE);

        Protocolize.registerService(JukeboxPlayableComponent.Factory.class, JukeboxPlayableComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(JukeboxPlayableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LlamaVariantComponent.Factory.class, LlamaVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(LlamaVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LockComponent.Factory.class, LockComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(LockComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LodestoneTrackerComponent.Factory.class, LodestoneTrackerComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(LodestoneTrackerComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LoreComponent.Factory.class, LoreComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(LoreComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapColorComponent.Factory.class, MapColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MapColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapDecorationsComponent.Factory.class, MapDecorationsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MapDecorationsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapIdComponent.Factory.class, MapIdComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MapIdComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapPostProcessingComponent.Factory.class, MapPostProcessingComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MapPostProcessingComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MaxDamageComponent.Factory.class, MaxDamageComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MaxDamageComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MaxStackSizeComponent.Factory.class, MaxStackSizeComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MaxStackSizeComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MooshroomVariantComponent.Factory.class, MooshroomVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(MooshroomVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(NoteBlockSoundComponent.Factory.class, NoteBlockSoundComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(NoteBlockSoundComponentImpl.Type.INSTANCE);

        Protocolize.registerService(OminousBottleAmplifierComponent.Factory.class, OminousBottleAmplifierComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(OminousBottleAmplifierComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PaintingVariantComponent.Factory.class, PaintingVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(PaintingVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ParrotVariantComponent.Factory.class, ParrotVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ParrotVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PigVariantComponent.Factory.class, PigVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(PigVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PotDecorationsComponent.Factory.class, PotDecorationsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(PotDecorationsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PotionContentsComponent.Factory.class, PotionContentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(PotionContentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PotionDurationScaleComponent.Factory.class, PotionDurationScaleComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(PotionDurationScaleComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ProfileComponent.Factory.class, ProfileComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ProfileComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ProvidesBannerPatternComponent.Factory.class, ProvidesBannerPatternComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ProvidesBannerPatternComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ProvidesTrimMaterialComponent.Factory.class, ProvidesTrimMaterialComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ProvidesTrimMaterialComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RabbitVariantComponent.Factory.class, RabbitVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(RabbitVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RarityComponent.Factory.class, RarityComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(RarityComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RecipesComponent.Factory.class, RecipesComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(RecipesComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RepairableComponent.Factory.class, RepairableComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(RepairableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RepairCostComponent.Factory.class, RepairCostComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(RepairCostComponentImpl.Type.INSTANCE);

        Protocolize.registerService(SalmonSizeComponent.Factory.class, SalmonSizeComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(SalmonSizeComponentImpl.Type.INSTANCE);

        Protocolize.registerService(SheepColorComponent.Factory.class, SheepColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(SheepColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ShulkerColorComponent.Factory.class, ShulkerColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ShulkerColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(StoredEnchantmentsComponent.Factory.class, StoredEnchantmentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(StoredEnchantmentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(SuspiciousStewEffectsComponent.Factory.class, SuspiciousStewEffectsComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(SuspiciousStewEffectsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ToolComponent.Factory.class, ToolComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(ToolComponentImpl.Type.INSTANCE);

        Protocolize.registerService(TooltipDisplayComponent.Factory.class, TooltipDisplayComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(TooltipDisplayComponentImpl.Type.INSTANCE);

        Protocolize.registerService(TooltipStyleComponent.Factory.class, TooltipStyleComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(TooltipStyleComponentImpl.Type.INSTANCE);

        Protocolize.registerService(TropicalFishBaseColorComponent.Factory.class, TropicalFishBaseColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(TropicalFishBaseColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(TropicalFishPatternColorComponent.Factory.class, TropicalFishPatternColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(TropicalFishPatternColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(TropicalFishPatternComponent.Factory.class, TropicalFishPatternComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(TropicalFishPatternComponentImpl.Type.INSTANCE);

        Protocolize.registerService(UnbreakableComponent.Factory.class, UnbreakableComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(UnbreakableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(UseCooldownComponent.Factory.class, UseCooldownComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(UseCooldownComponentImpl.Type.INSTANCE);

        Protocolize.registerService(UseRemainderComponent.Factory.class, UseRemainderComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(UseRemainderComponentImpl.Type.INSTANCE);

        Protocolize.registerService(VillagerVariantComponent.Factory.class, VillagerVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(VillagerVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WeaponComponent.Factory.class, WeaponComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(WeaponComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WolfCollarComponent.Factory.class, WolfCollarComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(WolfCollarComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WolfSoundVariantComponent.Factory.class, WolfSoundVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(WolfSoundVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WolfVariantComponent.Factory.class, WolfVariantComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(WolfVariantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WritableBookContentComponent.Factory.class, WritableBookContentComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(WritableBookContentComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WrittenBookContentComponent.Factory.class, WrittenBookContentComponentImpl.Type.INSTANCE);
        registrationProvider.registerDataComponentType(WrittenBookContentComponentImpl.Type.INSTANCE);

        PacketListenerProvider listenerProvider = Protocolize.listenerProvider();
        listenerProvider.registerListener(new CloseWindowListener(Direction.UPSTREAM));
        listenerProvider.registerListener(new CloseWindowListener(Direction.DOWNSTREAM));
        listenerProvider.registerListener(new ClickWindowListener());
        listenerProvider.registerListener(new PlayerPositionListener());
        listenerProvider.registerListener(new PlayerPositionLookListener());
        listenerProvider.registerListener(new PlayerLookListener());
        listenerProvider.registerListener(new UseItemListener());
        listenerProvider.registerListener(new BlockPlacementListener());
    }


}
