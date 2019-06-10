package org.tnmk.robocode.common.movement.antigravity;

import org.tnmk.robocode.common.paint.PaintHelper;
import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Point2D;

public class SimpleAntiGravityPainter {
    public static void paintForce(AdvancedRobot robot, Point2D force, Color color) {
        Graphics2D graphics = robot.getGraphics();
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        Point2D targetPosition = new Point2D.Double(robotPosition.getX() + force.getX(), robotPosition.getY() + force.getY());
        PaintHelper.paintLine(graphics, robotPosition, targetPosition, color);
    }
}
