package org.tnmk.robocode.common.helper;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.CircleMathUtils;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.movement.strategy.antigravity.AntiGravityMoveController;
import robocode.*;
import robocode.util.Utils;

public class Move2DUtils {
    public static final double ROBOT_SIZE = 50;

    public static Point2D reckonEnemyPosition(Robot thisRobot, HitRobotEvent targetRobotEvent) {
        return reckonEnemyPosition(thisRobot, targetRobotEvent.getBearing(), ROBOT_SIZE);
    }

    /**
     * This method is correct, no need to debug.
     *
     * @param thisRobot
     * @param targetRobotEvent
     * @return
     */
    public static Point2D reckonEnemyPosition(Robot thisRobot, ScannedRobotEvent targetRobotEvent) {
        return reckonEnemyPosition(thisRobot, targetRobotEvent.getBearing(), targetRobotEvent.getDistance());
    }

    /**
     * @param thisRobot
     * @param bearingToEnemyDegree
     * @param distanceToTarget
     * @return
     */
    public static Point2D reckonEnemyPosition(Robot thisRobot, double bearingToEnemyDegree, double distanceToTarget) {
        double angle = Math.toRadians(thisRobot.getHeading() + bearingToEnemyDegree);
        double x = (thisRobot.getX() + Math.sin(angle) * distanceToTarget);
        double y = (thisRobot.getY() + Math.cos(angle) * distanceToTarget);
        return new Point2D.Double(x, y);
    }

    /**
     * The robot will try to reach the destination eventually.<br/>
     * If the destination at the opposite direction, the robot still continue the current direction and try to turn direction regularly, eventually, but not immediately.<br/>
     * It means the robot won't reset and reverse its direction to reach the destination in the shortest way.<br/>
     * <p/>
     * However, it maybe very useful when using with {@link AntiGravityMoveController}.<br/>
     * View the reason in description of {@link #setMoveToDestinationWithShortestPath(AdvancedRobot, Point2D)} which try to reach the destination in the shortest direction.
     *
     * @param robot
     * @param destination
     */
    public static void setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(AdvancedRobot robot, Point2D destination) {
        Point2D currentPosition = new Point2D.Double(robot.getX(), robot.getY());
        double moveAngle = GeoMathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition.getX(), currentPosition.getY(), destination.getX(), destination.getY());
        robot.setTurnRight(moveAngle);
        robot.setAhead(Double.POSITIVE_INFINITY);//if you want it to reset at the destination, use setAhead(distance to destination + some additional distance for turning direction)
    }

    /**
     * View http://robowiki.net/wiki/GoTo<br/>
     * The robot will try to reach the destination as quick as possible.<br/>
     * If the destination at the opposite direction, the robot may reset, and reverse the movement direction.<br/>
     * <p/>
     * One caveat of this moment is that when using it in {@link AntiGravityMoveController}, the destination may be at the opposite direction with just a short distance.<br/>
     * In that case, robot just go back and forth in a short distance and become an easy victim.<br/>
     * To avoid that, we should use {@link #setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(AdvancedRobot, Point2D)} instead.<br/>
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
     * If pointB is outside limitArea, calculate another position for pointB inside limitArea which also on the same line.
     *
     * @param pointA    should be inside limitArea
     * @param pointB    could be outside limitArea
     * @param limitArea
     * @return if pointB is inside the limitArea, return pointB.
     */
    public static Point2D reckonMaximumDestination(Point2D pointA, Point2D pointB, Rectangle2D limitArea) {
        if (!GeoMathUtils.checkInsideRectangle(pointA, limitArea)) {
            return pointB;
        }

        Point2D newPointB = pointB;
        if (pointB.getY() > limitArea.getMaxY() && pointB.getY() > pointA.getY()) {
            double yC = limitArea.getMaxY();
            double xC = GeoMathUtils.calculateXOfPointCOnTheSameLine(pointA, newPointB, yC);
            newPointB = new Point2D.Double(xC, yC);
        } else if (pointB.getY() < limitArea.getMinY() && pointB.getY() < pointA.getY()) {
            double yC = limitArea.getMinY();
            double xC = GeoMathUtils.calculateXOfPointCOnTheSameLine(pointA, newPointB, yC);
            newPointB = new Point2D.Double(xC, yC);
        }

        //Don't use else here, we need to check both Y and X
        if (newPointB.getX() > limitArea.getMaxX() && newPointB.getX() > pointA.getX()) {
            double xC = limitArea.getMaxX();
            double yC = GeoMathUtils.calculateYOfPointCOnTheSameLine(pointA, newPointB, xC);
            newPointB = new Point2D.Double(xC, yC);
        } else if (newPointB.getX() < limitArea.getMinX() && newPointB.getX() < pointA.getX()) {
            double xC = limitArea.getMinX();
            double yC = GeoMathUtils.calculateYOfPointCOnTheSameLine(pointA, newPointB, xC);
            newPointB = new Point2D.Double(xC, yC);
        }

        return newPointB;
    }

    /**
     * @param robotPosition
     * @param newHeadingRadian this is the radian in-game angle (not the geometry angle)
     * @param normDistance     could be positive or negative
     * @return
     */
    public static Point2D reckonDestination(Point2D robotPosition, double newHeadingRadian, double normDistance) {
        //In geometry Maths, cos() is associated to x-coordinate, sin() is associated to y-coordinate.
        //But the angle in game is different from Geometry Maths, so sin() is associated to x now, and cos() is associated to y now.
        //To reverse the normal Geometry Maths formula, we change inGameAngle to Geometry angle.
        double geoNewHeadingRadian = AngleUtils.toGeometryRadian(newHeadingRadian);
        double x = robotPosition.getX() + Math.cos(geoNewHeadingRadian) * normDistance;
        double y = robotPosition.getY() + Math.sin(geoNewHeadingRadian) * normDistance;

//        double x = robotPosition.getX() + Math.sin(newHeadingRadian) * normDistance;
//        double y = robotPosition.getY() + Math.cos(newHeadingRadian) * normDistance;
        return new Point2D.Double(x, y);
    }

    /**
     * @param velocity
     * @return Assume a robot moves in a circle with unchanged velocity, unchanged turn rate, we'll calculate the time when it finish a circle.
     */
    public static double reckonFinishCircleTime(double velocity) {
        double turnRateRadians = Rules.getTurnRateRadians(velocity);
        return CircleMathUtils.calculateTimeToFinishCircle(turnRateRadians);
    }

    /**
     * This is not the remain distance compare to predefined {@link AdvancedRobot#setAhead(double)}.
     * This is the remain distance compare to the predefined param destination.
     *
     * @return
     */
    public static double reckonRemainDistanceToDestination(AdvancedRobot robot, Point2D destination) {
        Point2D robotPosition = BattleFieldUtils.constructRobotPosition(robot);
        double distanceToDestination = robotPosition.distance(destination);
        return distanceToDestination;
    }

    public static double reckonMoveAngleDegree(AdvancedRobot robot, Point2D destination){
        Point2D currentPosition = new Point2D.Double(robot.getX(), robot.getY());
        double moveAngle = GeoMathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition.getX(), currentPosition.getY(), destination.getX(), destination.getY());
        return moveAngle;
    }
}
