package org.tnmk.robocode.common.movement.runaway;

import java.awt.Color;
import java.awt.geom.Point2D;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.paint.PaintHelper;
import robocode.AdvancedRobot;

import static java.lang.Math.PI;

public class RunAwayHelper {
    public static void changeMovementWhenHit(AdvancedRobot robot, int currentMoveDirection, double hitBearingDegree) {
        debugCurrentMovement(robot, robot.getHeadingRadians(), currentMoveDirection);
        int newMoveDirection = -currentMoveDirection;

        //Don't need to go back 180 degree, turn 90 degree instead to avoid stuck back & forth forever.
        //If the newHeading is still fail (another HitRobotEvent will be triggered), the next time it will turn 90 degree again.
        //Eventually, it will find a void to get out of crash.
        double normHitBearingDegree = AngleUtils.normalizeDegree(hitBearingDegree);
        debugHitBearingAngle(robot, normHitBearingDegree);
        double newHeadingRadian;
        if (0 < normHitBearingDegree) {
            newHeadingRadian = robot.getHeadingRadians() - PI / 2;
        } else {
            newHeadingRadian = robot.getHeadingRadians() + PI / 2;
        }
        double newAhead = newMoveDirection * 200;

        robot.setTurnRightRadians(newHeadingRadian);
        robot.setAhead(newAhead);
        debugNewMovement(robot, newHeadingRadian, newAhead);
        LogHelper.logAdvanceRobot(robot, "Hit: start run away. Heading: " + robot.getHeading() + ", newHeading: " + AngleUtils.toDegree(newHeadingRadian) + ", direction: " + newMoveDirection + ", hitBearing: " + hitBearingDegree);

    }

    private static void debugCurrentMovement(AdvancedRobot robot, double currentHeadingRadian, double currentDirection) {
        LogHelper.logAdvanceRobot(robot, "Hit: headingRadian: " + currentHeadingRadian + ",\t heading: " + robot.getHeading()+",\t direction: "+currentDirection);
        debugAngleRadian(robot, currentHeadingRadian, currentDirection * 200, 2, Color.ORANGE);
    }

    private static void debugNewMovement(AdvancedRobot robot, double newHeadingRadian, double newAhead) {
        LogHelper.logAdvanceRobot(robot, "Hit: newHeadingRadian: " + newHeadingRadian + ",\t newHeading: " + AngleUtils.toDegree(newHeadingRadian) + ",\t distance: " + newAhead);
        debugAngleRadian(robot, newHeadingRadian, newAhead, 4, Color.YELLOW);
    }

    private static void debugHitBearingAngle(AdvancedRobot robot, double bearingDegree) {
        LogHelper.logAdvanceRobot(robot, "Hit: hitBearingRadian: " + AngleUtils.toRadian(bearingDegree) + ",\t bearingDegree: " + bearingDegree);
        debugAngleRadian(robot, AngleUtils.toRadian(bearingDegree), 200, 2, Color.RED);
    }

    /**
     * @param robot
     * @param angleRadian
     * @param normDistance could be negative or positive number.
     */
    private static void debugAngleRadian(AdvancedRobot robot, double angleRadian, double normDistance, int lineWeight, Color color) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        PaintHelper.paintAngleRadian(robot.getGraphics(), robotPosition, angleRadian, normDistance, lineWeight, color);
    }
}
