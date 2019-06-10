package org.tnmk.common.converter;

import org.tnmk.common.math.Point;

import java.awt.geom.Point2D;

public class PointConverter {
    public static Point toPoint(Point2D point2D){
        return new Point(point2D.getX(), point2D.getY());
    }
}
