package net.buildtheearth.terraplusplus.dataset.geojson.object;

import lombok.Data;
import lombok.NonNull;
import net.buildtheearth.terraplusplus.dataset.geojson.GeoJsonObject;
import net.buildtheearth.terraplusplus.dataset.geojson.Geometry;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Data
public final class Feature implements GeoJsonObject {
    @NonNull
    protected final Geometry geometry;
    protected final Map<String, String> properties;
    protected final String id;
}
