package dev.simplix.protocolize.data.plugin.generators;

import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.registries.Registries;
import net.bytebuddy.ByteBuddy;

import java.io.File;
import java.util.stream.Collectors;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public class ItemTypeGenerator implements Generator {

    private final File target;

    public ItemTypeGenerator(File target) {
        this.target = target;
        target.getParentFile().mkdirs();
    }

    @Override
    public void generate(Registries registries) throws Exception {
        new ByteBuddy().makeEnumeration(registries.itemRegistry().entries().keySet()
                .stream().map(s -> s.substring("minecraft:".length()).toUpperCase()).collect(Collectors.toList()))
            .name("dev.simplix.protocolize.data.ItemType")
            .make()
            .saveIn(target);
    }

}
