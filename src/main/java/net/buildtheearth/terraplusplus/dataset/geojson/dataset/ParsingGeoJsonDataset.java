package net.buildtheearth.terraplusplus.dataset.geojson.dataset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.NonNull;
import net.buildtheearth.terraplusplus.dataset.KeyedHttpDataset;
import net.buildtheearth.terraplusplus.dataset.geojson.GeoJson;
import net.buildtheearth.terraplusplus.dataset.geojson.GeoJsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author DaPorkchop_
 */
public class ParsingGeoJsonDataset extends KeyedHttpDataset<GeoJsonObject[]> {
    public ParsingGeoJsonDataset(@NonNull String[] urls) {
        super(urls);
    }

    @Override
    protected GeoJsonObject[] decode(@NonNull String path, @NonNull ByteBuf data) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufInputStream(data)))) { //parse each line as a GeoJSON object
            return reader.lines().map(GeoJson::parse).toArray(GeoJsonObject[]::new);
        }
    }
}
