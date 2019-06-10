package org.tnmk.robocode.common.movement.antigravity;

import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.paint.PaintHelper;
import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Point2D;

public class SimpleAntiGravityPainter {
    public static void paintStaticForces(Graphics graphics, AdvancedRobot robot, SimpleAntiGravityMovement.ForceResult staticForceResult) {
        paintForceResult(graphics, robot, staticForceResult, Color.GRAY);
    }

    private static void paintForceResult(Graphics graphics, AdvancedRobot robot, SimpleAntiGravityMovement.ForceResult forceResult, Color color) {
        for (Point2D force : forceResult.getForces()) {
            paintForce(graphics, robot, force, 1, color);
        }
        paintForce(graphics, robot, forceResult.getFinalForce(), 2, color);
    }

    public static void paintForce(Graphics graphics, AdvancedRobot robot, Point2D force, int width, Color color) {
        Point2D paintForce = Point2DUtils.multiple(force, 100000);
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        Point2D targetPosition = new Point2D.Double(robotPosition.getX() + paintForce.getX(), robotPosition.getY() + paintForce.getY());
        PaintHelper.paintLine(graphics, robotPosition, targetPosition, width, color);
    }

    public static void paintEnemiesForce(Graphics graphics, AdvancedRobot robot, SimpleAntiGravityMovement.ForceResult enemiesForceResult) {
        paintForceResult(graphics, robot, enemiesForceResult, Color.RED);
    }
}
