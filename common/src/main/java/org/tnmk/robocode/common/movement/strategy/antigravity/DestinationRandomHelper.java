package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DestinationRandomHelper {

    public static Point2D randomPointInAreas(List<Rectangle2D> areas){
        List<Point2D> randomPoints = new ArrayList<>();
        for (Rectangle2D area : areas) {
            Point2D randomPoint = randomPointInArea(area);
            randomPoints.add(randomPoint);
        }
        int ramdomIndex = new Random().nextInt(randomPoints.size());
        return randomPoints.get(ramdomIndex);
    }

    public static Point2D randomPointInArea(Rectangle2D area){
        double x = area.getX()+ Math.random()*area.getWidth();
        double y = area.getY()+ Math.random()*area.getHeight();
        return new Point2D.Double(x, y);
    }
}
