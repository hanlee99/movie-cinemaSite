package com.example.demo.util;

import org.locationtech.proj4j.*;

public class CoordinateConverter {
    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

    private static final CoordinateReferenceSystem EPSG_5174 =
            crsFactory.createFromName("EPSG:5174");
    private static final CoordinateReferenceSystem WGS84 =
            crsFactory.createFromName("EPSG:4326");

    private static final CoordinateTransform toWgs84Transform =
            ctFactory.createTransform(EPSG_5174, WGS84);
    private static final CoordinateTransform toEpsg5174Transform =
            ctFactory.createTransform(WGS84, EPSG_5174);

    public static double[] toWGS84(double x, double y) {
        ProjCoordinate src = new ProjCoordinate(x, y);
        ProjCoordinate result = new ProjCoordinate();
        toWgs84Transform.transform(src, result);
        return new double[]{result.y, result.x}; // [위도, 경도]
    }

    public static double[] toEPSG5174(double lat, double lon) {
        ProjCoordinate src = new ProjCoordinate(lon, lat);
        ProjCoordinate result = new ProjCoordinate();
        toEpsg5174Transform.transform(src, result);
        return new double[]{result.x, result.y}; // [x, y]
    }
}
