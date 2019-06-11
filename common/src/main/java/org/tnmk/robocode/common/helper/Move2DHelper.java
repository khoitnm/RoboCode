package org.tnmk.robocode.common.helper;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Optional;
import org.tnmk.common.math.MathUtils;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Move2DHelper implements Serializable {
    public static final double ROBOT_SIZE = 50;

    public static Point2D reckonTargetPosition(Robot thisRobot, HitRobotEvent targetRobotEvent) {
        return reckonTargetPosition(thisRobot, targetRobotEvent.getBearing(), ROBOT_SIZE);
    }

    /**
     * @param pointA
     * @param pointB
     * @param xC     x of PointC which is on the same line of PointA -> PointB
     * @return yC (y of PointC)
     */
    public static double reckonYOfPointCOnTheSameLine(Point2D pointA, Point2D pointB, double xC) {
        double yC = ((xC - pointA.getX()) / (pointB.getX() - pointA.getX()) * (pointB.getY() - pointA.getY())) + pointA.getY();
        return yC;
    }

    /**
     * @param pointA
     * @param pointB
     * @param yC     y of PointC which is on the same line of PointA -> PointB
     * @return xC (x of PointC)
     */
    public static double reckonXOfPointCOnTheSameLine(Point2D pointA, Point2D pointB, double yC) {
        double xC = ((yC - pointA.getY()) / (pointB.getY() - pointA.getY()) * (pointB.getX() - pointA.getX())) + pointA.getX();
        return xC;
    }

    /**
     * This method is correct, no need to debug.
     *
     * @param thisRobot
     * @param targetRobotEvent
     * @return
     */
    public static Point2D reckonTargetPosition(Robot thisRobot, ScannedRobotEvent targetRobotEvent) {
        return reckonTargetPosition(thisRobot, targetRobotEvent.getBearing(), targetRobotEvent.getDistance());
    }

    public static Point2D reckonTargetPosition(Robot thisRobot, double bearingToEnemy, double distanceToTarget) {
        double angle = Math.toRadians(thisRobot.getHeading() + bearingToEnemy);
        double x = (thisRobot.getX() + Math.sin(angle) * distanceToTarget);
        double y = (thisRobot.getY() + Math.cos(angle) * distanceToTarget);
        return new Point2D.Double(x, y);
    }

    public static void setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(AdvancedRobot robot, Point2D destination) {
        Point2D currentPosition = new Point2D.Double(robot.getX(), robot.getY());
        double moveAngle = MathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition.getX(), currentPosition.getY(), destination.getX(), destination.getY());
        robot.setTurnRight(moveAngle);
        robot.setAhead(Double.POSITIVE_INFINITY);//if you want it to stop at the destination, use setAhead(distance to destination + some additional distance for turning direction)
    }

    /**
     * View http://robowiki.net/wiki/GoTo
     *
     * @param robot
     * @param destination
     */
    public static void setMoveToDestinationWithShortestPath(AdvancedRobot robot, Point2D destination) {
        double destX = destination.getX();
        double destY = destination.getY();

        /* Calculate the difference bettwen the current position and the target position. */
        destX = destX - robot.getX();
        destY = destY - robot.getY();

        /* Calculate the angle relative to the current heading. */
        double goAngle = Utils.normalRelativeAngle(Math.atan2(destX, destY) - robot.getHeadingRadians());

        /*
         * Apply a tangent to the turn this is a cheap way of achieving back to front turn angle as tangents period is PI.
         * The output is very close to doing it correctly under most inputs. Applying the arctan will reverse the function
         * back into a normal value, correcting the value. The arctan is not needed if code size is required, the error from
         * tangent evening out over multiple turns.
         */
        robot.setTurnRightRadians(Math.atan(Math.tan(goAngle)));

        /*
         * The cosine call reduces the amount moved more the more perpendicular it is to the desired angle of travel. The
         * hypot is a quick way of calculating the distance to move as it calculates the length of the given coordinates
         * from 0.
         */
        robot.setAhead(Math.cos(goAngle) * Math.hypot(destX, destY));
    }

    /**
     * @param pointA    should be inside limitArea
     * @param pointB    could be outside limitArea
     * @param limitArea
     * @return if pointB is inside the limitArea, return empty.
     */
    public static Optional<Point2D> reckonMaximumDestination(Point2D pointA, Point2D pointB, Rectangle2D limitArea) {
        if (pointB.getY() > limitArea.getMaxY()) {
            double yC = limitArea.getMaxY();
            double xC = reckonXOfPointCOnTheSameLine(pointA, pointB, yC);
            return Optional.of(new Point2D.Double(xC, yC));
        } else if (pointB.getY() < limitArea.getMinY()) {
            double yC = limitArea.getMinY();
            double xC = reckonXOfPointCOnTheSameLine(pointA, pointB, yC);
            return Optional.of(new Point2D.Double(xC, yC));
        } else if (pointB.getX() > limitArea.getMaxX()) {
            double xC = limitArea.getMaxX();
            double yC = reckonYOfPointCOnTheSameLine(pointA, pointB, xC);
            return Optional.of(new Point2D.Double(xC, yC));
        } else if (pointB.getX() < limitArea.getMinX()) {
            double xC = limitArea.getMinX();
            double yC = reckonYOfPointCOnTheSameLine(pointA, pointB, xC);
            return Optional.of(new Point2D.Double(xC, yC));
        } else {
            return Optional.empty();
        }
    }
}
