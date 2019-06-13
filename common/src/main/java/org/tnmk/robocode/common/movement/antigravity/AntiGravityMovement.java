package org.tnmk.robocode.common.movement.antigravity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.helper.Move2DHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
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
 * Don't move follow the direction of the force. Instead, I change the {@link #reckonForceWeight(AntiGravityCalculationContext, double)} so that we can have an appropriate destination point inside the {@link AntiGravityCalculationContext#getSafeMovementArea()}.<br/>
 * Then when moving, I use {@link Move2DHelper#setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(AdvancedRobot, Point2D)} instead of {@link Move2DHelper#setMoveToDestinationWithShortestPath(AdvancedRobot, Point2D)}.<br/>
 */
public class AntiGravityMovement implements InitiableRun, OnScannedRobotControl {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;

    private AntiGravityCalculationContext calculationContext;

    public AntiGravityMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, MovementContext movementContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.movementContext = movementContext;
    }


    @Override
    public void runInit() {
        double battleWidth = robot.getBattleFieldWidth();
        double battleHeight = robot.getBattleFieldHeight();
        double safePaddingMovementDistance = 2 * (RobotPhysics.ROBOT_DISTANCE_TO_STOP_FROM_FULL_SPEED + RobotPhysics.ROBOT_SIZE * 2);


        Rectangle2D safeMovementArea = new Rectangle2D.Double(safePaddingMovementDistance, safePaddingMovementDistance, battleWidth - safePaddingMovementDistance * 2, battleHeight - safePaddingMovementDistance * 2);
        double maxPossibleMoveDistance = Math.min(battleWidth, battleHeight);
        int maxPossibleEnemiesCount = 1 + (int) (maxPossibleMoveDistance / RobotPhysics.ROBOT_SIZE);
        int maxActualEnemiesCount = robot.getOthers();

        double haftMaxPossibleMoveDistance = maxPossibleMoveDistance / 2;
        double safePaddingDistance = (maxPossibleEnemiesCount / 4) * RobotPhysics.ROBOT_SIZE;
        double maxSafeMoveDistance = Math.max(haftMaxPossibleMoveDistance, maxPossibleMoveDistance - safePaddingDistance);
        double movementIncrement = 10 * (maxPossibleEnemiesCount / maxActualEnemiesCount);

        calculationContext = new AntiGravityCalculationContext();
        calculationContext.setMaxActualEnemiesCount(maxActualEnemiesCount);
        calculationContext.setMaxPossibleEnemiesCount(maxPossibleEnemiesCount);
        calculationContext.setMaxPossibleMoveDistance(maxPossibleMoveDistance);
        calculationContext.setMaxSafeMoveDistance(maxSafeMoveDistance);
        calculationContext.setMovementIncrement(movementIncrement);
        calculationContext.setSafeMovementArea(safeMovementArea);

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
        Point2D force = reckonForce(this.calculationContext, this.robot, this.allEnemiesObservationContext);
        Point2D destination = Point2DUtils.plus(robotPosition, force);

        Optional<Point2D> avoidWallDestinationOptional = Move2DHelper.reckonMaximumDestination(robotPosition, destination, calculationContext.getSafeMovementArea());
        Point2D finalDestination = avoidWallDestinationOptional.orElse(destination);

//        Point2D finalDestination = WallSmoothUtils.wallSmoothing(
//                robotPosition,
//                destination,
//                GeoMathUtils.sign(robot.getHeading()), 200,
//                robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
//        LogHelper.logAdvanceRobot(robot, "Destination: " + LogHelper.toString(destination) + "\t Final Destination:" + LogHelper.toString(finalDestination));

        debugWhenFinalDestinationOutsideSafeArea(robot, robotPosition, destination, finalDestination, calculationContext.getSafeMovementArea());

        if (movementContext.isNone() || movementContext.is(MoveStrategy.ANTI_GRAVITY)) {
            AntiGravityPainterUtils.paintFinalDestination(robot, finalDestination);
            movementContext.setMoveStrategy(MoveStrategy.ANTI_GRAVITY);
            Move2DHelper.setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(robot, finalDestination);
        }
    }

    private void debugWhenFinalDestinationOutsideSafeArea(AdvancedRobot robot, Point2D robotPosition, Point2D destination, Point2D finalDestination, Rectangle2D safeMovementArea) {
        if (!GeoMathUtils.checkInsideRectangle(finalDestination, safeMovementArea)) {
            String message = String.format("Destination outside safeMovement: " +
                            "\t current %s" +
                            "\t destination %s" +
                            "\t finalDestination %s" +
                            "\t safe area %s",
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
    private static Point2D reckonForce(AntiGravityCalculationContext calculationContext, AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());

        Collection<Point2D> staticPositions = constructStaticPositions(robot);
        ForceResult staticForceResult = reckonForceOfStaticPositions(calculationContext, robotPosition, staticPositions);

        Collection<Enemy> enemies = allEnemiesObservationContext.getEnemies();
        ForceResult enemiesForceResult = reckonForceOfEnemies(calculationContext, robotPosition, enemies);

        Point2D finalForce = Point2DUtils.plus(staticForceResult.getFinalForce(), enemiesForceResult.getFinalForce());
//        AntiGravityPainterUtils.paintForceResults(robot, staticForceResult, enemiesForceResult, finalForce);
        return finalForce;
    }


    /**
     * @param robotPosition
     * @param enemies
     * @return absolute vector of the force.
     */
    private static ForceResult reckonForceOfEnemies(AntiGravityCalculationContext calculationContext, Point2D robotPosition, Collection<Enemy> enemies) {
        Point2D finalForce = new Point2D.Double();
        List<Point2D> forces = new ArrayList<>();

        for (Enemy enemy : enemies) {
            Point2D enemyPosition = enemy.getPosition();
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(enemyPosition, robotPosition);
            double distance = enemyPosition.distance(robotPosition);
            double forceWeight = reckonForceWeight(calculationContext, distance);
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
        Collection<Point2D> wallsPositions = constructWallsPositions(robot);
        Point2D middleBattlePosition = new Point2D.Double(robot.getBattleFieldWidth() / 2, robot.getBattleFieldHeight() / 2);
        Collection<Point2D> staticPositions = new ArrayList<>(wallsPositions);
        staticPositions.add(middleBattlePosition);
        return staticPositions;
    }

    private static Collection<Point2D> constructWallsPositions(Robot robot) {
        Point2D closestHorizonTopWallPosition = new Point2D.Double(robot.getX(), robot.getBattleFieldHeight());
        Point2D closestHorizonBottomWallPosition = new Point2D.Double(robot.getX(), 0);
        Point2D closestVerticalLeftWallPosition = new Point2D.Double(0, robot.getY());
        Point2D closestVerticalRightWallPosition = new Point2D.Double(robot.getBattleFieldWidth(), robot.getY());
        Collection<Point2D> staticPositions = Arrays.asList(
                closestHorizonBottomWallPosition
                , closestHorizonTopWallPosition
                , closestVerticalLeftWallPosition
                , closestVerticalRightWallPosition
        );
        return staticPositions;
    }

    private static ForceResult reckonForceOfStaticPositions(AntiGravityCalculationContext calculationContext, Point2D robotPosition, Collection<Point2D> positions) {
        Point2D finalForce = new Point2D.Double();
        List<Point2D> forces = new ArrayList<>();
        for (Point2D position : positions) {
            double absBearing = reckonAbsoluteBearingBetweenTwoPoints(position, robotPosition);
            double distance = position.distance(robotPosition);
            double forceWeight = reckonForceWeight(calculationContext, distance);

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
    private static double reckonForceWeight(AntiGravityCalculationContext calculationContext, double distance) {
        double result = calculationContext.getMaxSafeMoveDistance() / Math.pow(distance, 2) + calculationContext.getMovementIncrement() * calculationContext.getMaxSafeMoveDistance() / distance;
        result = Math.min(result, calculationContext.getMaxSafeMoveDistance());
        return result;
    }

    private static double reckonAbsoluteBearingBetweenTwoPoints(Point2D pointA, Point2D pointB) {
        double absBearing = Utils.normalAbsoluteAngle(Math.atan2(pointA.getX() - pointB.getX(), pointA.getY() - pointB.getY()));
        return absBearing;
    }


}
