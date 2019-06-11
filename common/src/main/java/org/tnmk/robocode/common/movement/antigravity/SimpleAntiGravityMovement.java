package org.tnmk.robocode.common.movement.antigravity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * View more at http://robowiki.net/wiki/Anti-Gravity_Tutorial
 * One weakness: the direction change so quickly which make robot cannot adapt. As a result, it cannot move fast and far.
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
            double turnRightRadian = Utils.normalRelativeAngle(angle - robot.getHeadingRadians());
            robot.setTurnRightRadians(turnRightRadian);
            robot.setAhead(Double.POSITIVE_INFINITY);
        } else {
            double turnRightRadian = Utils.normalRelativeAngle(angle + Math.PI - robot.getHeadingRadians());
            robot.setTurnRightRadians(turnRightRadian);
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

        Collection<Point2D> staticPositions = constructStaticPositions(robot);
        ForceResult staticForceResult = reckonForceOfStaticPositions(robotPosition, staticPositions);

        Collection<Enemy> enemies = allEnemiesObservationContext.getEnemies();
        ForceResult enemiesForceResult = reckonForceOfEnemies(robotPosition, enemies);

        Point2D finalForce = Point2DUtils.plus(staticForceResult.getFinalForce(), enemiesForceResult.getFinalForce());
        paintForceResults(robot, staticForceResult, enemiesForceResult, finalForce);
        return finalForce;
    }

    private static void paintForceResults(AdvancedRobot robot, ForceResult staticForceResult, ForceResult enemiesForceResult, Point2D finalForce) {
        Graphics2D graphics = robot.getGraphics();
        SimpleAntiGravityPainterUtils.paintStaticForces(graphics, robot, staticForceResult);
        SimpleAntiGravityPainterUtils.paintEnemiesForce(graphics, robot, enemiesForceResult);
        SimpleAntiGravityPainterUtils.paintForce(graphics, robot, finalForce, 3, Color.GREEN);
    }

    /**
     * @param robotPosition
     * @param enemies
     * @return absolute vector of the force.
     */
    private static ForceResult reckonForceOfEnemies(Point2D robotPosition, Collection<Enemy> enemies) {
        Point2D finalForce = new Point2D.Double();
        List<Point2D> forces = new ArrayList<>();

        for (Enemy enemy : enemies) {
            Point2D enemyPosition = enemy.getPosition();
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(enemyPosition, robotPosition);
            double distance = enemyPosition.distance(robotPosition);
            double forceWeight = 1 / (distance * distance);
            Point2D force = new Point2D.Double(-(Math.sin(absBearing) * forceWeight), -(Math.cos(absBearing) * forceWeight));
            finalForce = Point2DUtils.plus(finalForce, force);
            forces.add(force);
        }
        ForceResult forceResult = new ForceResult(forces, finalForce);
        return forceResult;
    }

    private static Collection<Point2D> constructStaticPositions(Robot robot) {
        Point2D closestHorizonTopWallPosition = new Point2D.Double(robot.getX(), robot.getBattleFieldHeight());
        Point2D closestHorizonBottomWallPosition = new Point2D.Double(robot.getX(), 0);
        Point2D closestVerticalLeftWallPosition = new Point2D.Double(0, robot.getY());
        Point2D closestVerticalRightWallPosition = new Point2D.Double(robot.getBattleFieldWidth(), robot.getY());
        Point2D middleBattlePosition = new Point2D.Double(robot.getBattleFieldWidth() / 2, robot.getBattleFieldHeight() / 2);
        Collection<Point2D> staticPositions = Arrays.asList(
                closestHorizonBottomWallPosition
                , closestHorizonTopWallPosition
                , closestVerticalLeftWallPosition
                , closestVerticalRightWallPosition
                , middleBattlePosition
        );
        return staticPositions;
    }

    private static ForceResult reckonForceOfStaticPositions(Point2D robotPosition, Collection<Point2D> positions) {
        Point2D finalForce = new Point2D.Double();
        List<Point2D> forces = new ArrayList<>();
        for (Point2D position : positions) {
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(position, robotPosition);
            double distance = position.distance(robotPosition);
            double forceWeight = 1 / (distance * distance);
            Point2D force = new Point2D.Double(-(Math.sin(absBearing) * forceWeight), -(Math.cos(absBearing) * forceWeight));

            forces.add(force);
            finalForce = Point2DUtils.plus(finalForce, force);
        }
        ForceResult forceResult = new ForceResult(forces, finalForce);
        return forceResult;
    }

    private static double reckonAbsoluteBearingBetweenTwoPoints(Point2D pointA, Point2D pointB) {
        double absBearing = Utils.normalAbsoluteAngle(Math.atan2(pointA.getX() - pointB.getX(), pointA.getY() - pointB.getY()));
        return absBearing;
    }


}
