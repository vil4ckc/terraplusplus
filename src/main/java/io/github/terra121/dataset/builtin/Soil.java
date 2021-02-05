package io.github.terra121.dataset.builtin;

import io.github.terra121.util.RLEByteArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.common.function.io.IOSupplier;
import net.daporkchop.lib.common.ref.Ref;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.InputStream;

import static net.daporkchop.lib.common.math.PMath.*;

public class Soil extends AbstractBuiltinDataset {
    protected static final int COLS = 10800;
    protected static final int ROWS = 5400;

    private static final Ref<RLEByteArray> DATA_CACHE = Ref.soft((IOSupplier<RLEByteArray>) () -> {
        ByteBuf buf;
        try (InputStream in = new BZip2CompressorInputStream(Climate.class.getResourceAsStream("soil.bz2"))) {
            buf = Unpooled.wrappedBuffer(StreamUtil.toByteArray(in));
        }

        RLEByteArray.Builder builder = RLEByteArray.builder();
        for (int i = 0, lim = buf.readableBytes(); i < lim; i++) {
            builder.append(buf.getByte(i));
        }
        return builder.build();
    });

    private final RLEByteArray data = DATA_CACHE.get();

    public Soil() {
        super(COLS, ROWS);
    }

    @Override
    protected double get(double fx, double fy) {
        int x = floorI(fx);
        int y = floorI(fy);
        if (x >= COLS || x < 0 || y >= ROWS || y < 0) {
            return 0;
        }
        return this.data.get(y * COLS + x);
    }
}