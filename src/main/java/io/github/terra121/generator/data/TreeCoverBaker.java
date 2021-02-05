package io.github.terra121.generator.data;

import io.github.terra121.dataset.scalar.ScalarDataset;
import io.github.terra121.generator.CachedChunkData;
import io.github.terra121.generator.EarthGeneratorPipelines;
import io.github.terra121.generator.GeneratorDatasets;
import io.github.terra121.projection.OutOfProjectionBoundsException;
import io.github.terra121.util.CornerBoundingBox2d;
import io.github.terra121.util.bvh.Bounds2d;
import net.minecraft.util.math.ChunkPos;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static io.github.terra121.generator.EarthGeneratorPipelines.*;
import static net.daporkchop.lib.common.math.PMath.*;

/**
 * @author DaPorkchop_
 */
public class TreeCoverBaker implements IEarthDataBaker<double[]> {
    public static final double TREE_AREA = 3.0d * 3.0d; //the surface area covered by an average tree

    public static final byte[] FALLBACK_TREE_DENSITY = new byte[16 * 16];

    static {
        Arrays.fill(FALLBACK_TREE_DENSITY, treeChance(50.0d));
    }

    static byte treeChance(double value) {
        if (Double.isNaN(value)) {
            return 0;
        }

        //value is in range [0-1]
        value *= (1.0 / TREE_AREA);

        //scale to byte range
        value *= 255.0d;

        //increase by 50%
        value *= 1.50d;

        return (byte) clamp(ceilI(value), 0, 255);
    }

    @Override
    public CompletableFuture<double[]> requestData(ChunkPos pos, GeneratorDatasets datasets, Bounds2d bounds, CornerBoundingBox2d boundsGeo) throws OutOfProjectionBoundsException {
        return datasets.<ScalarDataset>getCustom(KEY_DATASET_TREE_COVER).getAsync(boundsGeo, 16, 16);
    }

    @Override
    public void bake(ChunkPos pos, CachedChunkData.Builder builder, double[] treeCover) {
        byte[] arr = new byte[16 * 16];
        if (treeCover != null) {
            for (int i = 0; i < 16 * 16; i++) {
                arr[i] = treeChance(treeCover[i]);
            }
        }
        builder.putCustom(EarthGeneratorPipelines.KEY_DATA_TREE_COVER, arr);
    }
}
