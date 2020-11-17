package com.matrixeater.src;

import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.GeosetVertex;
import com.hiveworkshop.wc3.mdl.Normal;
import com.hiveworkshop.wc3.mdl.TVertex;

public class ModelUtils {

    static GeosetVertex addGeosetAndTVerticies(Geoset newGeoset, int vX, int vY, int vZ, int tX, int tY) {
        final GeosetVertex vertex = new GeosetVertex(vX, (double) vY / 2, vZ, new Normal(0, 0, 1));
        final TVertex tVert = new TVertex(tX, tY);
        vertex.addTVertex(tVert);
        newGeoset.add(vertex);
        vertex.setGeoset(newGeoset);
        return vertex;
    }
}
