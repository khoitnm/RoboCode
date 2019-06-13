package org.tnmk.robocode.common.movement.runaway;

import java.awt.Color;
import java.awt.geom.Point2D;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.helper.Move2DHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.paint.PaintHelper;
import robocode.AdvancedRobot;

import static java.lang.Math.PI;

public class RunAwayHelper {
    public static void changeMovementWhenHit(AdvancedRobot robot, int currentMoveDirection, double hitBearing) {
        int newMoveDirection = -currentMoveDirection;

        //Don't need to go back 180 degree, turn 90 degree instead to avoid stuck back & forth forever.
        //If the newHeading is still fail (another HitRobotEvent will be triggered), the next time it will turn 90 degree again.
        //Eventually, it will find a void to get out of crash.
        double normHitBearing = AngleUtils.normalizeDegree(hitBearing);
        double newHeadingRadian;
        if (0 < normHitBearing) {
            newHeadingRadian = robot.getHeadingRadians() - PI / 2;
        } else {
            newHeadingRadian = robot.getHeadingRadians() + PI / 2;
        }
        double newAhead = newMoveDirection * 200;

        robot.setTurnRightRadians(newHeadingRadian);
        robot.setAhead(newAhead);
        debugPaint(robot, newHeadingRadian, newAhead);
        LogHelper.logAdvanceRobot(robot, "Hit enemy: start run away " + robot.getHeading() + ", new heading: " + AngleUtils.toDegree(newHeadingRadian) + ", direction: " + newMoveDirection);
    }

    private static void debugPaint(AdvancedRobot robot, double newHeadingRadian, double newAhead) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        Point2D destination = Move2DHelper.reckonDestination(robotPosition, newHeadingRadian, newAhead);
        PaintHelper.paintLine(robot.getGraphics(), robotPosition, destination, 4, Color.YELLOW);
    }
}
