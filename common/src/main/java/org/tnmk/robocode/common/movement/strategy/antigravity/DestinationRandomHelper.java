package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DestinationRandomHelper {

    public static Point2D randomPointsAroundCentralInAreas(List<Rectangle2D> areas) {
        List<Point2D> randomPoints = new ArrayList<>();
        for (Rectangle2D area : areas) {
            Point2D randomPoint = randomPointAroundCentralOfArea(area);
            randomPoints.add(randomPoint);
        }
        int ramdomIndex = new Random().nextInt(randomPoints.size());
        return randomPoints.get(ramdomIndex);
    }

    public static Point2D randomPointAroundCentralOfArea(Rectangle2D area) {
        double oneFifthWidth = area.getWidth() / 5;
        double oneFifthHeight = area.getHeight() / 5;
        double x = area.getX() + 2 * oneFifthWidth + Math.random() * oneFifthWidth;//This make the random point around the central of the area.
        double y = area.getY() + 2 * oneFifthHeight + Math.random() * oneFifthHeight;//This make the random point around the central of the area.
        return new Point2D.Double(x, y);
    }
}
