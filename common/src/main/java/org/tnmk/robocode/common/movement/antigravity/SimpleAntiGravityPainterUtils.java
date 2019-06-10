package org.tnmk.robocode.common.movement.antigravity;

import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.paint.PaintHelper;
import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Point2D;

public class SimpleAntiGravityPainterUtils {
    private static final double FORCE_LENGTH = 30000000d;//This value suitable for battle field 1000x1000

    public static void paintStaticForces(Graphics2D graphics, AdvancedRobot robot, ForceResult staticForceResult) {
        paintForceResult(graphics, robot, staticForceResult, Color.GRAY);
    }

    private static void paintForceResult(Graphics2D graphics, AdvancedRobot robot, ForceResult forceResult, Color color) {
        for (Point2D force : forceResult.getForces()) {
            paintForce(graphics, robot, force, 1, color);
        }
        paintForce(graphics, robot, forceResult.getFinalForce(), 3, color);
    }

    public static void paintForce(Graphics2D graphics, AdvancedRobot robot, Point2D force, int width, Color color) {
        Point2D paintForce = Point2DUtils.multiple(force, FORCE_LENGTH);
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        Point2D targetPosition = new Point2D.Double(robotPosition.getX() + paintForce.getX(), robotPosition.getY() + paintForce.getY());
        PaintHelper.paintLine(graphics, robotPosition, targetPosition, width, color);
    }

    public static void paintEnemiesForce(Graphics2D graphics, AdvancedRobot robot, ForceResult enemiesForceResult) {
        paintForceResult(graphics, robot, enemiesForceResult, Color.RED);
    }
}
