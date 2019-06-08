package org.tnmk.common.converter;

import org.tnmk.common.math.Point;

import java.awt.geom.Point2D;

public class Point2DDoubleConverter {
    public static Point2D.Double toPoint2DDouble(Point point){
        return new Point2D.Double(point.getX(), point.getY());
    }
}
