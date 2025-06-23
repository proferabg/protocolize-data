package dev.simplix.protocolize.data.registries;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Registries {

    @SerializedName("minecraft:item")
    private ItemRegistry itemRegistry;

    @SerializedName("minecraft:sound_event")
    private GenericRegistry soundRegistry;

    @SerializedName("minecraft:mob_effect")
    private GenericRegistry mobEffectRegistry;

    @SerializedName("minecraft:potion")
    private GenericRegistry potionRegistry;

    @SerializedName("minecraft:enchantment")
    private GenericRegistry enchantmentRegistry;

    @SerializedName("minecraft:attribute")
    private GenericRegistry attributeRegistry;

    @SerializedName("minecraft:instrument")
    private GenericRegistry instrumentRegistry;

    @SerializedName("minecraft:data_component_type")
    private GenericRegistry dataComponentTypeRegistry;

    @SerializedName("minecraft:entity_type")
    private GenericRegistry entityTypeRegistry;

    @SerializedName("minecraft:consume_effect_type")
    private GenericRegistry consumeEffectTypeRegistry;

    @SerializedName("minecraft:block")
    private GenericRegistry blockRegistry;

    @SerializedName("minecraft:data_component_predicate_type")
    private GenericRegistry getDataComponentPredicateTypeRegistry;

    @SerializedName("minecraft:damage_type")
    private GenericRegistry damageTypeRegistry;

    @SerializedName("minecraft:trim_material")
    private GenericRegistry trimMaterialRegistry;

    @SerializedName("minecraft:trim_pattern")
    private GenericRegistry trimPatternRegistry;

    @SerializedName("minecraft:armor_material")
    private GenericRegistry armorMaterialRegistry;

    @SerializedName("minecraft:banner_pattern")
    private GenericRegistry bannerPatternRegistry;

    @SerializedName("minecraft:axolotl_variant")
    private GenericRegistry axolotlVariantRegistry;

    @SerializedName("minecraft:villager_type")
    private GenericRegistry villagerTypeRegistry;

    @SerializedName("minecraft:wolf_variant")
    private GenericRegistry wolfVariantRegistry;

    @SerializedName("minecraft:wolf_sound_variant")
    private GenericRegistry wolfSoundVariantRegistry;

    @SerializedName("minecraft:fox_variant")
    private GenericRegistry foxVariantRegistry;

    @SerializedName("minecraft:salmon_variant")
    private GenericRegistry salmonVariantRegistry;

    @SerializedName("minecraft:parrot_variant")
    private GenericRegistry parrotVariantRegistry;

    @SerializedName("minecraft:tropical_fish_pattern")
    private GenericRegistry tropicalFishPatternRegistry;

    @SerializedName("minecraft:mooshroom_variant")
    private GenericRegistry mooshroomVariantRegistry;

    @SerializedName("minecraft:rabbit_variant")
    private GenericRegistry rabbitVariantRegistry;

    @SerializedName("minecraft:pig_variant")
    private GenericRegistry pigVariantRegistry;

    @SerializedName("minecraft:cow_variant")
    private GenericRegistry cowVariantRegistry;

    @SerializedName("minecraft:chicken_variant")
    private GenericRegistry chickenVariantRegistry;

    @SerializedName("minecraft:frog_variant")
    private GenericRegistry frogVariantRegistry;

    @SerializedName("minecraft:horse_variant")
    private GenericRegistry horseVariantRegistry;

    @SerializedName("minecraft:painting_variant")
    private GenericRegistry paintingVariantRegistry;

    @SerializedName("minecraft:llama_variant")
    private GenericRegistry llamaVariantRegistry;

    @SerializedName("minecraft:cat_variant")
    private GenericRegistry catVariantRegistry;
}
