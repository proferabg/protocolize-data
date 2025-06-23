package dev.simplix.protocolize.data.plugin;

import com.google.gson.Gson;
import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.plugin.generators.GenericGenerator;
import dev.simplix.protocolize.data.registries.GenericRegistry;
import dev.simplix.protocolize.data.registries.Registries;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import net.obvj.jsonmerge.JsonMerger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Mojo(name = "compile")
public class ProtocolizeDataMojo extends AbstractMojo {

    private static final File CLASSES_DIR = new File("protocolize-data-bundle/target/classes/");
    private static final Gson GSON = new Gson();
    private static final Map<Integer, Registries> parsedRegistries = new HashMap<>();

    @SneakyThrows
    @Override
    public void execute() {
        Registries latestRegistries = getRegistry(MINECRAFT_LATEST);

        List<Generator> generators = new ArrayList<>(Arrays.asList(
            new GenericGenerator(CLASSES_DIR, latestRegistries.itemRegistry(), "ItemType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.soundRegistry(), "Sound"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.mobEffectRegistry(), "MobEffect"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.potionRegistry(), "Potion"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.attributeRegistry(), "Attribute"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.consumeEffectTypeRegistry(), "ConsumeEffectType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.entityTypeRegistry(), "EntityType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.blockRegistry(), "Block"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.enchantmentRegistry(), "Enchantment"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.instrumentRegistry(), "Instrument"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.damageTypeRegistry(), "DamageType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.trimMaterialRegistry(), "TrimMaterial"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.trimPatternRegistry(), "TrimPattern"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.armorMaterialRegistry(), "ArmorMaterial"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.bannerPatternRegistry(), "BannerPattern"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.villagerTypeRegistry(), "VillagerType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.wolfVariantRegistry(), "WolfVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.wolfSoundVariantRegistry(), "WolfSoundVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.foxVariantRegistry(), "FoxVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.salmonVariantRegistry(), "SalmonVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.parrotVariantRegistry(), "ParrotVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.tropicalFishPatternRegistry(), "TropicalFishPattern"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.mooshroomVariantRegistry(), "MooshroomVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.rabbitVariantRegistry(), "RabbitVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.pigVariantRegistry(), "PigVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.cowVariantRegistry(), "CowVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.chickenVariantRegistry(), "ChickenVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.frogVariantRegistry(), "FrogVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.horseVariantRegistry(), "HorseVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.paintingVariantRegistry(), "PaintingVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.llamaVariantRegistry(), "LlamaVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.axolotlVariantRegistry(), "AxolotlVariant"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.axolotlVariantRegistry(), "CatVariant")
        ));


        /*
         * This will register all DataComponentType's for all versions 1.20.5-Latest as some were already removed.
         * It will still use the mapping provider to grab correct versions or warn if they cannot be used on a
         * specific version.
         */
        List<GenericRegistry> dataComponentRegistries = new ArrayList<>();
        for(Registries registry : getRegistries(MINECRAFT_1_20_5, MINECRAFT_LATEST)){
            try {
                dataComponentRegistries.add(registry.dataComponentTypeRegistry());
            } catch (Exception ignored) { }
        }
        generators.add(new GenericGenerator(CLASSES_DIR, dataComponentRegistries, "DataComponentType"));

        List<GenericRegistry> dataComponentPredicateRegistries = new ArrayList<>();
        for(Registries registry : getRegistries(MINECRAFT_1_21_5, MINECRAFT_LATEST)){
            try {
                dataComponentPredicateRegistries.add(registry.getDataComponentPredicateTypeRegistry());
            } catch (Exception ignored) { }
        }
        generators.add(new GenericGenerator(CLASSES_DIR, dataComponentPredicateRegistries, "DataComponentPredicateType"));

        for (Generator generator : generators) {
            long now = System.currentTimeMillis();
            getLog().info("Running generator for " + generator.getName() + "...");
            generator.generate();
            getLog().info("Generator for " + generator.getName() + " done (" + (System.currentTimeMillis() - now) + " ms)");
        }
    }

    private Registries getRegistry(int protocolVersion) throws FileNotFoundException {
        if(parsedRegistries.containsKey(protocolVersion)){
            return parsedRegistries.get(protocolVersion);
        } else {
            try {
                Registries registry;
                String minecraftRegistryStr = readFile(new File("protocolize-data-bundle/src/main/resources/registries/" + protocolVersion + "/registries.json").getAbsolutePath());

                // some items like enchantments, instruments, and damage types are not in the generated registries.json
                // so we can use an extended.json file for any internal or custom registries and merge them with the unmodified registries.json
                File extendedRegistry = new File("protocolize-data-bundle/src/main/resources/registries/" + protocolVersion + "/extended.json");
                if(extendedRegistry.exists()){
                    String extendedRegistryStr = readFile(extendedRegistry.getAbsolutePath());
                    // extended registry first so that it take president in case of wanting to overwrite/patch registries
                    minecraftRegistryStr = new JsonMerger<>(JSONObject.class).merge(extendedRegistryStr, minecraftRegistryStr).toString();
                }

                registry = GSON.fromJson(minecraftRegistryStr, Registries.class);
                parsedRegistries.put(protocolVersion, registry);
                return registry;
            } catch (Exception ignored){
                getLog().warn("Failed to get registry for version " + protocolVersion);
            }
        }
        return null;
    }

    private List<Registries> getRegistries(int startVersion, int endVersion) throws FileNotFoundException {
        List<Registries> registries = new ArrayList<>();
        for(int i = startVersion; i <= endVersion; i++){
            Registries registry = getRegistry(i);
            if(registry != null){
                registries.add(registry);
            }
        }
        return registries;
    }

    private String readFile(String path){
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            getLog().warn("Failed to read file " + path);
            return "";
        }
    }

}
