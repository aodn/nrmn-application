package au.org.aodn.nrmn.restapi.dto;

import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class BoundingBoxDto {

    Double xmin, ymin, xmax, ymax;

    public BoundingBoxDto(String coord1, String coord2) {

        if(StringUtils.isEmpty(coord1) || StringUtils.isEmpty(coord2))
            return;
            
        var arrCoord1 = coord1.split(",");
        var x1 = Double.valueOf(arrCoord1[0]);
        var y1 = Double.valueOf(arrCoord1[1]);

        var arrCoord2 = coord2.split(",");
        var x2 = Double.valueOf(arrCoord2[0]);
        var y2 = Double.valueOf(arrCoord2[1]);

        this.xmin = Math.min(x1, x2);
        this.ymin = Math.min(y1, y2);
        this.xmax = Math.max(x1, x2);
        this.ymax = Math.max(y1, y2);
    }

    public boolean valid() {

        if(xmin == null || ymin == null || xmax == null || ymax == null)
            return false;

        if (this.xmin > 90 || this.xmin < -90)
            return false;

        if (this.xmax > 90 || this.xmax < -90)
            return false;

        if (this.ymin < -180 || this.ymin > 180)
            return false;

        if (this.ymax < -180 || this.ymax > 180)
            return false;

        return true;
    }

    public Geometry getGeometry() {
        if(!valid())
            return null;
        return new GeometryFactory().toGeometry(new Envelope(xmin, xmax, ymin, ymax));
    }
}
