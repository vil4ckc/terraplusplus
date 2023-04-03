package net.buildtheearth.terraplusplus.projection.wkt.misc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import net.buildtheearth.terraplusplus.projection.wkt.AbstractWKTObject;
import net.buildtheearth.terraplusplus.projection.wkt.WKTWriter;
import net.buildtheearth.terraplusplus.projection.wkt.unit.WKTAngleUnit;
import net.buildtheearth.terraplusplus.projection.wkt.unit.WKTValueInDegreeOrValueAndUnit;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@JsonIgnoreProperties("$schema")
@Jacksonized
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Getter
public final class WKTPrimeMeridian extends AbstractWKTObject.WithNameAndID {
    @NonNull
    private final WKTValueInDegreeOrValueAndUnit longitude;

    @Override
    public void write(@NonNull WKTWriter writer) throws IOException {
        writer.beginObject("PRIMEM")
                .writeQuotedLatinString(this.name())
                .writeSignedNumericLiteral(this.longitude.value())
                .writeOptionalObject(this.longitude.unit())
                .writeOptionalObject(this.id())
                .endObject();
    }
}
