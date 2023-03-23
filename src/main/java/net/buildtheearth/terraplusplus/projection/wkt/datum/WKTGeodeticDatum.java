package net.buildtheearth.terraplusplus.projection.wkt.datum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.buildtheearth.terraplusplus.projection.wkt.misc.WKTBoundingBox;
import net.buildtheearth.terraplusplus.projection.wkt.misc.WKTEllipsoid;
import net.buildtheearth.terraplusplus.projection.wkt.WKTWriter;
import net.buildtheearth.terraplusplus.projection.wkt.misc.WKTPrimeMeridian;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Getter
public abstract class WKTGeodeticDatum extends WKTDatum {
    @NonNull
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    private final WKTEllipsoid ellipsoid;

    @Builder.Default
    private final String scope = null;

    @Builder.Default
    private final String area = null;

    @NonNull
    private final WKTBoundingBox bbox;

    @Builder.Default
    @JsonProperty("prime_meridian")
    private final WKTPrimeMeridian primeMeridian = null;

    @Override
    public void write(@NonNull WKTWriter writer) throws IOException {
        writer.beginObject("DATUM")
                .writeRequiredObject(this.ellipsoid)
                .writeRequiredObject(this.bbox)
                .writeOptionalObject(this.id())
                .endObject()
                .writeOptionalObject(this.primeMeridian);
    }
}
