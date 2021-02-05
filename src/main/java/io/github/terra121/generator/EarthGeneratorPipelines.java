package io.github.terra121.generator;

import io.github.terra121.dataset.builtin.Climate;
import io.github.terra121.dataset.builtin.Soil;
import io.github.terra121.dataset.osm.OpenStreetMap;
import io.github.terra121.dataset.scalar.MultiresScalarDataset;
import io.github.terra121.event.InitDatasetsEvent;
import io.github.terra121.event.InitEarthRegistryEvent;
import io.github.terra121.generator.biome.IEarthBiomeFilter;
import io.github.terra121.generator.biome.Terra121BiomeFilter;
import io.github.terra121.generator.data.HeightsBaker;
import io.github.terra121.generator.data.IEarthDataBaker;
import io.github.terra121.generator.data.InitialBiomesBaker;
import io.github.terra121.generator.data.NullIslandBaker;
import io.github.terra121.generator.data.OSMBaker;
import io.github.terra121.generator.data.TreeCoverBaker;
import io.github.terra121.generator.populate.BiomeDecorationPopulator;
import io.github.terra121.generator.populate.CompatibilityEarthPopulators;
import io.github.terra121.generator.populate.IEarthPopulator;
import io.github.terra121.generator.populate.SnowPopulator;
import io.github.terra121.generator.populate.TreePopulator;
import io.github.terra121.util.OrderedRegistry;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Array;
import java.util.Map;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Default processing pipelines for various earth generator processing steps.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class EarthGeneratorPipelines {
    public final String KEY_DATASET_HEIGHTS = "heights";
    public final String KEY_DATASET_OSM = "osm";
    public final String KEY_DATASET_TERRA121_PRECIPITATION = "terra121_precipitation";
    public final String KEY_DATASET_TERRA121_SOIL = "terra121_soil";
    public final String KEY_DATASET_TERRA121_TEMPERATURE = "terra121_temperature";
    public final String KEY_DATASET_TREE_COVER = "tree_cover";

    public final String KEY_DATA_TREE_COVER = "tree_cover";

    private <T> T[] fire(@NonNull InitEarthRegistryEvent<T> event) {
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.registry().entryStream().map(Map.Entry::getValue).toArray(i -> uncheckedCast(Array.newInstance(event.getGenericType(), i)));
    }

    public Map<String, Object> datasets(@NonNull EarthGeneratorSettings settings) {
        InitDatasetsEvent event = new InitDatasetsEvent(settings);

        event.register(KEY_DATASET_HEIGHTS, new MultiresScalarDataset(KEY_DATASET_HEIGHTS, settings.useDefaultHeights()));
        event.register(KEY_DATASET_OSM, new OpenStreetMap(settings));
        event.register(KEY_DATASET_TERRA121_PRECIPITATION, new Climate.Precipitation());
        event.register(KEY_DATASET_TERRA121_SOIL, new Soil());
        event.register(KEY_DATASET_TERRA121_TEMPERATURE, new Climate.Temperature());
        event.register(KEY_DATASET_TREE_COVER, new MultiresScalarDataset(KEY_DATASET_TREE_COVER, settings.useDefaultTreeCover()));

        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getAllCustomProperties();
    }

    public IEarthBiomeFilter<?>[] biomeFilters(@NonNull EarthGeneratorSettings settings) {
        return fire(new InitEarthRegistryEvent<IEarthBiomeFilter>(settings,
                uncheckedCast(new OrderedRegistry<IEarthBiomeFilter<?>>()
                        .addLast("legacy_terra121", new Terra121BiomeFilter()))) {});
    }

    public IEarthDataBaker<?>[] dataBakers(@NonNull EarthGeneratorSettings settings) {
        return fire(new InitEarthRegistryEvent<IEarthDataBaker>(settings,
                uncheckedCast(new OrderedRegistry<IEarthDataBaker<?>>()
                        .addLast("initial_biomes", new InitialBiomesBaker(settings.biomeProvider()))
                        .addLast("tree_cover", new TreeCoverBaker())
                        .addLast("heights", new HeightsBaker())
                        .addLast("osm", new OSMBaker())
                        .addLast("null_island", new NullIslandBaker()))) {});
    }

    public IEarthPopulator[] populators(@NonNull EarthGeneratorSettings settings) {
        return fire(new InitEarthRegistryEvent<IEarthPopulator>(settings,
                new OrderedRegistry<IEarthPopulator>()
                        .addLast("fml_pre_cube_populate_event", CompatibilityEarthPopulators.cubePopulatePre())
                        .addLast("trees", new TreePopulator())
                        .addLast("biome_decorate", new BiomeDecorationPopulator(settings))
                        .addLast("snow", new SnowPopulator())
                        .addLast("fml_post_cube_populate_event", CompatibilityEarthPopulators.cubePopulatePost())
                        .addLast("cc_cube_generators_registry", CompatibilityEarthPopulators.cubeGeneratorsRegistry())) {});
    }
}
