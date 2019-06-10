package org.tnmk.robocode.common.movement.antigravity;

import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.radar.scanall.Enemy;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

/**
 * View more at http://robowiki.net/wiki/Anti-Gravity_Tutorial
 */
public class SimpleAntiGravityMovement implements Scannable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    public SimpleAntiGravityMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }


    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Point2D force = reckonForce(this.robot, this.allEnemiesObservationContext);
        moveFollowTheForce(this.robot, force);
    }

    private static void moveFollowTheForce(AdvancedRobot robot, Point2D force) {
        double angle = Math.atan2(force.getX(), force.getY());

        if (force.getY() == 0 && force.getY() == 0) {
            // If no force, do nothing
        } else if (Math.abs(angle - robot.getHeadingRadians()) < Math.PI / 2) {
            robot.setTurnRightRadians(Utils.normalRelativeAngle(angle - robot.getHeadingRadians()));
            robot.setAhead(Double.POSITIVE_INFINITY);
        } else {
            robot.setTurnRightRadians(Utils.normalRelativeAngle(angle + Math.PI - robot.getHeadingRadians()));
            robot.setAhead(Double.NEGATIVE_INFINITY);
        }
    }

    /**
     * @param robot                        your robot
     * @param allEnemiesObservationContext store all information of enemy robots.
     * @return
     */
    private static Point2D reckonForce(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());

        double forceX = 0, forceY = 0;
        Collection<Enemy> enemies = allEnemiesObservationContext.getEnemies();
        Point2D closestHorizonTopWallPosition = new Point2D.Double(robot.getX(), robot.getBattleFieldHeight());
        Point2D closestHorizonBottomWallPosition = new Point2D.Double(robot.getX(), 0);
        Point2D closestVerticalLeftWallPosition = new Point2D.Double(0, robot.getY());
        Point2D closestVerticalRightWallPosition = new Point2D.Double(robot.getBattleFieldWidth(), robot.getY());
        Point2D middleBattlePosition = new Point2D.Double(robot.getBattleFieldWidth() / 2, robot.getBattleFieldHeight() / 2);
        Collection<Point2D> staticPositions = Arrays.asList(
                closestHorizonBottomWallPosition,
                closestHorizonTopWallPosition,
                closestVerticalLeftWallPosition,
                closestVerticalRightWallPosition,
                middleBattlePosition
        );
        Point2D staticForce = reckonForceOfStaticPositions(robotPosition, staticPositions);
        Point2D enemiesForce = reckonForceOfEnemies(robotPosition, enemies);

        Point2D force = Point2DUtils.plus(staticForce, enemiesForce);
        return force;
    }

    /**
     * @param robotPosition
     * @param enemies
     * @return absolute vector of the force.
     */
    private static Point2D reckonForceOfEnemies(Point2D robotPosition, Collection<Enemy> enemies) {
        double forceX = 0, forceY = 0;

        for (Enemy enemy : enemies) {
            Point2D enemyPosition = enemy.getPosition();
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(enemyPosition, robotPosition);
            double distance = enemyPosition.distance(robotPosition);
            double forceWeight = 1 / (distance * distance) / enemy.getEnergy();
            forceX -= Math.sin(absBearing) * forceWeight;
            forceY -= Math.cos(absBearing) * forceWeight;
        }
        Point2D force = new Point2D.Double(forceX, forceY);
        return force;
    }

    private static Point2D reckonForceOfStaticPositions(Point2D robotPosition, Collection<Point2D> positions) {
        double forceX = 0, forceY = 0;

        for (Point2D position : positions) {
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(position, robotPosition);
            double distance = position.distance(robotPosition);
            double forceWeight = 1 / (distance * distance);
            forceX -= Math.sin(absBearing) * forceWeight;
            forceY -= Math.cos(absBearing) * forceWeight;
        }
        Point2D force = new Point2D.Double(forceX, forceY);
        return force;
    }

    private static double reckonAbsoluteBearingBetweenTwoPoints(Point2D pointA, Point2D pointB) {
        double absBearing = Utils.normalAbsoluteAngle(Math.atan2(pointA.getX() - pointB.getX(), pointA.getY() - pointB.getY()));
        return absBearing;
    }
}
