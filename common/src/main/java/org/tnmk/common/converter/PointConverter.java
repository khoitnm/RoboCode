package org.tnmk.common.converter;

import org.tnmk.common.math.Point;

import java.awt.geom.Point2D;

public class PointConverter {
    public static Point toPoint(Point2D.Double point2DDouble){
        return new Point(point2DDouble.x, point2DDouble.y);
    }
}
