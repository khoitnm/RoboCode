package org.tnmk.robocode.common.movement.antigravity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.helper.Move2DHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.SpecialMovementType;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * View more at http://robowiki.net/wiki/Anti-Gravity_Tutorial
 * <p/>
 * One weakness in the original tutorial:<br/>
 * the direction change so quickly which make robot cannot adapt. As a result, it cannot move fast and far.
 * <p/>
 * My improvement:<br/>
 * Don't move follow the direction of the force. Instead, I change the {@link #reckonForceWeight(double)} so that we can have an appropriate destination point inside the {@link #safeMovementArea}.<br/>
 * Then when moving, I use {@link Move2DHelper#setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(AdvancedRobot, Point2D)} instead of {@link Move2DHelper#setMoveToDestinationWithShortestPath(AdvancedRobot, Point2D)}.<br/>
 */
public class AntiGravityMovement implements InitiableRun, Scannable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;

    private static Rectangle2D safeMovementArea;
    private static double maxPossibleMoveDistance;
    private static double maxSafeMoveDistance;
    private static int maxPossibleEnemiesCount;
    private static int maxActualEnemiesCount;
    private static double movementIncrement;
    private static double maxForceWithoutHittingWall;


    public AntiGravityMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, MovementContext movementContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.movementContext = movementContext;
    }


    @Override
    public void runInit() {
        double battleWidth = robot.getBattleFieldWidth();
        double battleHeight = robot.getBattleFieldHeight();
        double safePaddingMovementDistance = (RobotPhysics.ROBOT_DISTANCE_TO_STOP_FROM_FULL_SPEED + RobotPhysics.ROBOT_SIZE * 2) * 2;
        safeMovementArea = new Rectangle2D.Double(safePaddingMovementDistance, safePaddingMovementDistance, battleWidth - safePaddingMovementDistance * 2, battleHeight - safePaddingMovementDistance * 2);
        maxPossibleMoveDistance = Math.min(battleWidth, battleHeight);
        maxPossibleEnemiesCount = 1 + (int) (maxPossibleMoveDistance / RobotPhysics.ROBOT_SIZE);
        maxActualEnemiesCount = robot.getOthers();

        double haftMaxPossibleMoveDistance = maxPossibleMoveDistance / 2;
        double safePaddingDistance = (maxPossibleEnemiesCount / 4) * RobotPhysics.ROBOT_SIZE;
        maxSafeMoveDistance = Math.max(haftMaxPossibleMoveDistance, maxPossibleMoveDistance - safePaddingDistance);

        movementIncrement = 10 * (maxPossibleEnemiesCount / maxActualEnemiesCount);
        maxForceWithoutHittingWall = this.maxPossibleMoveDistance - RobotPhysics.ROBOT_DISTANCE_TO_STOP_FROM_FULL_SPEED;

        robot.out.println("maxPossibleMoveDistance" + maxPossibleMoveDistance);
        robot.out.println("maxPossibleEnemiesCount" + maxPossibleEnemiesCount);
        robot.out.println("maxActualEnemiesCount" + maxActualEnemiesCount);
        robot.out.println("maxSafeMoveDistance" + maxSafeMoveDistance);
        robot.out.println("movementIncrement" + movementIncrement);

    }

    //TODO it only change movement when seeing updated enemies. If radar somehow doesn't work as expected, it just stay still!!!
    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        Point2D force = reckonForce(this.robot, this.allEnemiesObservationContext);
        Point2D destination = Point2DUtils.plus(robotPosition, force);

        Optional<Point2D> avoidWallDestinationOptional = Move2DHelper.reckonMaximumDestination(robotPosition, destination, safeMovementArea);
        Point2D finalDestination = avoidWallDestinationOptional.orElse(destination);

//        Point2D finalDestination = WallSmoothUtils.wallSmoothing(
//                robotPosition,
//                destination,
//                MathUtils.sign(robot.getHeading()), 200,
//                robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
//        LogHelper.logAdvanceRobot(robot, "Destination: " + LogHelper.toString(destination) + "\t Final Destination:" + LogHelper.toString(finalDestination));

        debugWhenFinalDestinationOutsideSafeArea(robot, robotPosition, destination, finalDestination, safeMovementArea);

        AntiGravityPainterUtils.paintFinalDestination(robot, finalDestination);

        if (movementContext.isNone() || movementContext.is(SpecialMovementType.ANTI_GRAVITY)) {
            Move2DHelper.setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(robot, finalDestination);
        }
    }

    private void debugWhenFinalDestinationOutsideSafeArea(AdvancedRobot robot, Point2D robotPosition, Point2D destination, Point2D finalDestination, Rectangle2D safeMovementArea) {
        if (!Move2DHelper.checkInsideRectangle(finalDestination, safeMovementArea)) {
            String message = String.format("Destination outside safeMovement: current %s\t destination %s\t finalDestination %s\t safe area %s",
                    LogHelper.toString(robotPosition),
                    LogHelper.toString(destination),
                    LogHelper.toString(finalDestination),
                    LogHelper.toString(safeMovementArea));
            LogHelper.logAdvanceRobot(robot, message);
        }
    }

    /**
     * @param robot                        your robot
     * @param allEnemiesObservationContext store all information of enemy robots.
     * @return
     */
    private Point2D reckonForce(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());

        Collection<Point2D> staticPositions = constructStaticPositions(robot);
        ForceResult staticForceResult = reckonForceOfStaticPositions(robotPosition, staticPositions);

        Collection<Enemy> enemies = allEnemiesObservationContext.getEnemies();
        ForceResult enemiesForceResult = reckonForceOfEnemies(robotPosition, enemies);

        Point2D finalForce = Point2DUtils.plus(staticForceResult.getFinalForce(), enemiesForceResult.getFinalForce());
        AntiGravityPainterUtils.paintForceResults(robot, staticForceResult, enemiesForceResult, finalForce);
        return finalForce;
    }


    /**
     * @param robotPosition
     * @param enemies
     * @return absolute vector of the force.
     */
    private ForceResult reckonForceOfEnemies(Point2D robotPosition, Collection<Enemy> enemies) {
        Point2D finalForce = new Point2D.Double();
        List<Point2D> forces = new ArrayList<>();

        for (Enemy enemy : enemies) {
            Point2D enemyPosition = enemy.getPosition();
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(enemyPosition, robotPosition);
            double distance = enemyPosition.distance(robotPosition);
            double forceWeight = reckonForceWeight(distance);
            Point2D force = new Point2D.Double(-(Math.sin(absBearing) * forceWeight), -(Math.cos(absBearing) * forceWeight));
//            robot.out.println(String.format("enemy \t name %s \t forceWeight %.2f ", enemy.getName(), forceWeight));
//            robot.out.println(String.format("enemy \t name %s \t force %s ", enemy.getName(), force));

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

    private ForceResult reckonForceOfStaticPositions(Point2D robotPosition, Collection<Point2D> positions) {
        Point2D finalForce = new Point2D.Double();
        List<Point2D> forces = new ArrayList<>();
        for (Point2D position : positions) {
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(position, robotPosition);
            double distance = position.distance(robotPosition);
            double forceWeight = reckonForceWeight(distance);

            Point2D force = new Point2D.Double(-(Math.sin(absBearing) * forceWeight), -(Math.cos(absBearing) * forceWeight));
//            robot.out.println(String.format("wall \t wall %s \t forceWeight %.2f", position, forceWeight));
//            robot.out.println(String.format("wall \t wall %s \t force %s ", position, force));

            forces.add(force);
            finalForce = Point2DUtils.plus(finalForce, force);
        }
        ForceResult forceResult = new ForceResult(forces, finalForce);
        return forceResult;
    }

    /**
     * @param distance
     * @return
     */
    private double reckonForceWeight(double distance) {
        double result = maxSafeMoveDistance / Math.pow(distance, 2) + movementIncrement * maxSafeMoveDistance / distance;
        result = Math.min(result, maxSafeMoveDistance);
        return result;
    }

    private static double reckonAbsoluteBearingBetweenTwoPoints(Point2D pointA, Point2D pointB) {
        double absBearing = Utils.normalAbsoluteAngle(Math.atan2(pointA.getX() - pointB.getX(), pointA.getY() - pointB.getY()));
        return absBearing;
    }


}
