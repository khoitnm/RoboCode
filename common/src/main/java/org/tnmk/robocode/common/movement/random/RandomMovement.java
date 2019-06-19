package org.tnmk.robocode.common.movement.random;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.stream.Collectors;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.helper.Move2DHelper;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

/**
 * FIXME when running test, I got this error:
 * <pre>
 * Waiting for robot org.tnmk.robocode.robot.TheUnfoldingRobot* to stop thread org.tnmk.robocode.robot.TheUnfoldingRobot*
 * Robot org.tnmk.robocode.robot.TheUnfoldingRobot* is not stopping.  Forcing a stop.
 * org.tnmk.robocode.robot.TheUnfoldingRobot* stopped successfully.
 * org.tnmk.robocode.robot.TheUnfoldingRobot* has been stopped.
 * org.tnmk.robocode.robot.TheUnfoldingRobot* cannot be stopped.
 *
 * More debug:
 * There's an error caused by :throw new IllegalStateException("Some thing really wrong with our code, the aroundEnemyOnTheSameSide should be always not empty when move closer to the enemy.");
 * And at that time, program hung when 2 robots are so close together (distance is 1 or 0???)
 * </pre>
 *
 * Note: the above bug may be fixed!??? I just change that when the distance is too close, return destination is the enemy's position.
 * --------------------------------------------------------------
 * FIXME sometimes my robot stay still for so long.
 */
public class RandomMovement implements LoopableRun, OnScannedRobotControl {
    /**
     * Measure unit: pixel.
     */
    private static final double DISTANCE_2_POTENTIAL_DESTINATIONS = 50;
    private static final double CHANGE_DISTANCE = 100;
    private static final int MIN_ACCEPTABLE_SAME_SIDE_POINTS = 4;

    private static final Color ALL_POTENTIAL_POINTS_COLORS = Color.GRAY;
    private static final Color SAME_SIDE_POINTS_COLORS = Color.ORANGE;
    private static final Color OTHER_SIDE_POINTS_COLORS = Color.RED;
    private static final Color DESTINATION_COLOR = Color.YELLOW;
    private static final int DEBUG_POINT_SIZE = 3;
    private static final int DEBUG_FINAL_POINT_SIZE = 10;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;

    public RandomMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, MovementContext movementContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.movementContext = movementContext;
    }

    private long startTime = 0;
    private long estimateFinishTime = 0;

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Rectangle2D battleField = Move2DHelper.constructBattleField(robot);
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        double oldEnemyEnergy = getOldEnemyEnergy(scannedRobotEvent.getName());
        String energies = allEnemiesObservationContext.getEnemyPatternPrediction(scannedRobotEvent.getName())
                .getEnemyHistory()
                .getLatestHistoryItems(5).stream()
                .map(enemy -> "" + enemy.getEnergy())
                .collect(Collectors.joining(","));
//        System.out.println("Enemies " + energies);
        boolean isEnemyFired = suspectEnemyHasJustFiredBullet(oldEnemyEnergy, scannedRobotEvent.getEnergy());
//        if (isEnemyFired) {
//            System.out.println("Enemy " + scannedRobotEvent.getName() + " fired " + isEnemyFired);
//        }

        if (movementContext.hasLowerPriority(MoveStrategy.RANDOM)) {
            movementContext.setMoveStrategy(MoveStrategy.RANDOM);
            startTime = robot.getTime();
            boolean isChangeMovement;

            //FIXME, the second last doesn't have the latest data.
            if (isEnemyFired) {
                isChangeMovement = true;
//                LogHelper.logAdvanceRobot(robot, "enemy " + scannedRobotEvent.getName() + " has just fired");
            } else {
                isChangeMovement = Math.random() < .2;
            }
            if (isChangeMovement) {//80% will turn direction randomly.
                Enemy enemy = allEnemiesObservationContext.getEnemy(scannedRobotEvent.getName());
                Point2D enemyPosition = enemy.getPosition();
                Point2D destination;
                double absoluteTurnAngleToEnemy;
                if (robot.getEnergy() / scannedRobotEvent.getEnergy() > 3 || robot.getEnergy() - scannedRobotEvent.getEnergy() > 30d) {
                    destination = randomDestinationCloserToEnemy(robotPosition, enemyPosition, enemy.getDistance(), DISTANCE_2_POTENTIAL_DESTINATIONS, battleField);
//
//                    absoluteTurnAngleToEnemy = randomAngleMoveTowardEnemy(scannedRobotEvent);
                    DebugHelper.debugMoveRandomTowardEnemy(robot);
                } else {
                    if (scannedRobotEvent.getDistance() < Math.min(robot.getBattleFieldWidth(), robot.getBattleFieldHeight()) * 0.75) {
                        destination = randomDestinationFurtherFromEnemy(robotPosition, enemyPosition, enemy.getDistance(), DISTANCE_2_POTENTIAL_DESTINATIONS, battleField);
//                        absoluteTurnAngleToEnemy = randomAngleMoveFarAwayFromEnemy(scannedRobotEvent);
                        DebugHelper.debugMoveRandomFarAwayEnemy(robot);
                    } else {
                        destination = randomDestinationAroundEnemy(robotPosition, enemyPosition, enemy.getDistance(), DISTANCE_2_POTENTIAL_DESTINATIONS, battleField);
//                        absoluteTurnAngleToEnemy = randomAngleMoveNearlyPerpendicularToEnemy(scannedRobotEvent);
                        DebugHelper.debugMoveRandomPerpendicularEnemy(robot);
                    }
                }
                double destinationDistance = robotPosition.distance(destination);
                long estimationRunningTime = Math.min(Math.round(destinationDistance / Rules.MAX_VELOCITY), 15);
                estimateFinishTime = robot.getTime() + estimationRunningTime;
                System.out.println(String.format("[%s] estimate running time: %s, estimate finish time: %s", robot.getTime(), estimationRunningTime, estimateFinishTime));
                if (Math.random() < 0.75) {
                    Move2DHelper.setMoveToDestinationWithShortestPath(robot, destination);
//                    movementContext.reverseDirection();
//                    absoluteTurnAngleToEnemy = AngleUtils.normalizeDegree(absoluteTurnAngleToEnemy + 180);
                } else {
                    Move2DHelper.setMoveToDestinationWithCurrentDirectionButDontStopAtDestination(robot, destination);
                }
//                robot.setTurnRight(absoluteTurnAngleToEnemy);
            } else {
                /** Keep the same movement, doesn't change anything. */
            }
//            robot.setAhead(movementContext.getDirection() * 125);
        } else if (movementContext.is(MoveStrategy.RANDOM) && robot.getTime() >= estimateFinishTime) {
            movementContext.setNone();
        }
    }

    private Point2D randomDestinationCloserToEnemy(Point2D robotPosition, Point2D enemyPosition, double currentDistance, double distanceBetweenPoints, Rectangle2D movementArea) {
        double newDistance = currentDistance - CHANGE_DISTANCE;
        if (newDistance <= 10) {
            return enemyPosition;
        }
        List<Point2D> aroundEnemyPositions = constructSurroundPositions(enemyPosition, newDistance, distanceBetweenPoints);
        List<Point2D> aroundEnemyPositionsWithinMovementArea = choosePointsInsideArea(aroundEnemyPositions, movementArea);
        int i = 0;
        while (aroundEnemyPositionsWithinMovementArea.isEmpty()) {
            //It means that the currentDistance is too small compare to distanceBetweenPoints, we need to reduce distanceBetweenPoints
            distanceBetweenPoints /= 2d;
            aroundEnemyPositions = constructSurroundPositions(enemyPosition, newDistance, distanceBetweenPoints);
            aroundEnemyPositionsWithinMovementArea = choosePointsInsideArea(aroundEnemyPositions, movementArea);
            i++;
            if (i > 20) {
                LogHelper.logAdvanceRobot(robot, "THERE ARE SOMETHING WRONG!!!: NO aroundEnemyPositionsWithinMovementArea" +
                        "\n\trandomDestinationCloserToEnemy()" +
                        "\n\trobotPosition: " + robotPosition +
                        "\n\tenemyPosition: " + enemyPosition +
                        "\n\tcurrentDistance: " + currentDistance +
                        "\n\tnewDistance: " + newDistance +
                        "\n\tdistanceBetweenPoints: " + distanceBetweenPoints +
                        "\n\taroundEnemyPositionsWithinMovementArea: " + aroundEnemyPositionsWithinMovementArea.size()
                );
                return enemyPosition;
            }
        }

        PointsOnSide pointsOnSide = distinguishSideOfPositions(robotPosition, enemyPosition, newDistance, aroundEnemyPositionsWithinMovementArea);
        if (pointsOnSide.sameSide.isEmpty()) {
            LogHelper.logAdvanceRobot(robot, "THERE ARE SOMETHING WRONG!!!: NO sameSide: " +
                    "\n\trandomDestinationCloserToEnemy()" +
                    "\n\trobotPosition: " + robotPosition +
                    "\n\tenemyPosition: " + enemyPosition +
                    "\n\tcurrentDistance: " + currentDistance +
                    "\n\tnewDistance: " + newDistance +
                    "\n\tdistanceBetweenPoints: " + distanceBetweenPoints +
                    "\n\taroundEnemyPositionsWithinMovementArea: " + aroundEnemyPositionsWithinMovementArea.size()
            );
            throw new IllegalStateException("Some thing really wrong with our code, the aroundEnemyOnTheSameSide should be always not empty when move closer to the enemy.");
        }
        return randomDestination(pointsOnSide.sameSide);
    }

    private Point2D randomDestinationFurtherFromEnemy(Point2D robotPosition, Point2D enemyPosition, double currentDistance, double distanceBetweenPoints, Rectangle2D movementArea) {
        double newDistance = currentDistance + CHANGE_DISTANCE;
        return randomDestinationWithFixedDistance(robotPosition, enemyPosition, newDistance, distanceBetweenPoints, movementArea);
    }

    private Point2D randomDestinationAroundEnemy(Point2D robotPosition, Point2D enemyPosition, double currentDistance, double distanceBetweenPoints, Rectangle2D movementArea) {
        double distanceDelta = (Math.random() * CHANGE_DISTANCE) - (Math.random() * CHANGE_DISTANCE);
        double newDistance = currentDistance + distanceDelta;
        return randomDestinationWithFixedDistance(robotPosition, enemyPosition, newDistance, distanceBetweenPoints, movementArea);
    }

    private Point2D randomDestinationWithFixedDistance(Point2D robotPosition, Point2D enemyPosition, double currentDistance, double distanceBetweenPoints, Rectangle2D movementArea) {
        List<Point2D> aroundEnemyPositions = constructSurroundPositions(enemyPosition, currentDistance, distanceBetweenPoints);
        List<Point2D> aroundEnemyPositionsWithinMovementArea = choosePointsInsideArea(aroundEnemyPositions, movementArea);
        while (aroundEnemyPositionsWithinMovementArea.size() < 5) {
            currentDistance = Math.abs(currentDistance - CHANGE_DISTANCE);
            aroundEnemyPositions = constructSurroundPositions(enemyPosition, currentDistance, distanceBetweenPoints);
            aroundEnemyPositionsWithinMovementArea = choosePointsInsideArea(aroundEnemyPositions, movementArea);
        }

        PointsOnSide pointsOnSide = distinguishSideOfPositions(robotPosition, enemyPosition, currentDistance, aroundEnemyPositionsWithinMovementArea);
        if (pointsOnSide.sameSide.size() >= MIN_ACCEPTABLE_SAME_SIDE_POINTS) {
            return randomDestination(pointsOnSide.sameSide);
        } else {
            Set<Point2D> potentialPoints = new HashSet<>();
            potentialPoints.addAll(pointsOnSide.sameSide);
            int halfOfOtherSide = (pointsOnSide.otherSide.size() - 1) / 2;
            for (int i = 0; i < halfOfOtherSide && potentialPoints.size() < (MIN_ACCEPTABLE_SAME_SIDE_POINTS + 2); i++) {
                potentialPoints.add(pointsOnSide.otherSide.get(i));
                potentialPoints.add(pointsOnSide.otherSide.get((pointsOnSide.otherSide.size() - 1) - i));
            }
            return randomDestination(new ArrayList<>(potentialPoints));
        }
    }
//
//    /**
//     * @param enemyPosition
//     * @param destinationDistanceFromEnemy the distance from the enemy to surround destination points
//     * @param movementArea                 the destination positions will be generated inside the movementAre only
//     * @return if cannot found any good destination,
//     */
//    private List<Point2D> constructSurroundPositions(Point2D myPosition, Point2D enemyPosition, double destinationDistanceFromEnemy, double distanceBetween2Points) {
//        List<Point2D> aroundEnemyPositions = constructSurroundPositions(enemyPosition, destinationDistanceFromEnemy, distanceBetween2Points);
//        return aroundEnemyPositions;
//    }

    private Point2D randomDestination(List<Point2D> potentialDestinations) {
        for (Point2D potential : potentialDestinations) {
            PaintHelper.paintPoint(robot.getGraphics(), DEBUG_POINT_SIZE, SAME_SIDE_POINTS_COLORS, potential, null);
        }

        int destinationIndex = (int) Math.round(Math.random() * (potentialDestinations.size() - 1));
        Point2D destination = potentialDestinations.get(destinationIndex);
        PaintHelper.paintPoint(robot.getGraphics(), DEBUG_FINAL_POINT_SIZE, DESTINATION_COLOR, destination, null);
        return destination;
    }

    private List<Point2D> choosePointsInsideArea(List<Point2D> originalPositions, Rectangle2D area) {
        List<Point2D> result = originalPositions.stream()
                .filter(position -> GeoMathUtils.checkInsideRectangle(position, area))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * @param pointA
     * @param pointB                      the root point of surround position
     * @param radiusFromBToSurroundPoints from PointB to surround positions
     * @param surroundPositions           surround positions of pointB
     * @return within the surround positions of pointB, find which point on the same side of pointA
     */
    private PointsOnSide distinguishSideOfPositions(Point2D pointA, Point2D pointB, double radiusFromBToSurroundPoints, List<Point2D> surroundPositions) {
        List<Point2D> sameSide = new ArrayList<>();
        List<Point2D> otherSide = new ArrayList<>();
        double distanceAB = pointA.distance(pointB);
        double furthestAcceptedDistance = Math.sqrt(Math.pow(distanceAB, 2) + Math.pow(radiusFromBToSurroundPoints, 2));
        for (Point2D surroundPosition : surroundPositions) {
            if (pointA.distance(surroundPosition) <= furthestAcceptedDistance) {
                sameSide.add(surroundPosition);
            } else {
                otherSide.add(surroundPosition);
            }
        }
        for (Point2D destination : sameSide) {
            PaintHelper.paintPoint(robot.getGraphics(), DEBUG_POINT_SIZE, SAME_SIDE_POINTS_COLORS, destination, null);
        }
        for (Point2D destination : sameSide) {
            PaintHelper.paintPoint(robot.getGraphics(), DEBUG_POINT_SIZE, OTHER_SIDE_POINTS_COLORS, destination, null);
        }
        return new PointsOnSide(sameSide, otherSide);
    }

    /**
     * @param rootPosition                   act as the root of the surrounding circle.
     * @param distanceFromRoot               act as the radius of the surrounding circle.
     * @param distanceBetweenPotentialPoints the distance between result points. This distance don't need to be correct. It's just an estimation how you want to distributed result points (and how many result points you want to have).
     *                                       the function will automatically re-balance the number of points for you.
     * @return
     */
    private List<Point2D> constructSurroundPositions(Point2D rootPosition, double distanceFromRoot, double distanceBetweenPotentialPoints) {
        List<Point2D> result = new ArrayList<>();
        double totalRadianOfACircle = 2d * Math.PI;
        double expectingRadianAngleBetweenTwoPositions = distanceBetweenPotentialPoints / distanceFromRoot;
        int numResultPositions = (int) Math.round(totalRadianOfACircle / expectingRadianAngleBetweenTwoPositions);
        if (numResultPositions == 0) {
            numResultPositions = 1;
        }
        double actualRadianAngleBetweenTwoPositions = totalRadianOfACircle / numResultPositions;
        for (double radian = 0; radian < totalRadianOfACircle; radian += actualRadianAngleBetweenTwoPositions) {
            Point2D destination = GeoMathUtils.calculateDestinationPoint(rootPosition, radian, distanceFromRoot);
            result.add(destination);
        }
        for (Point2D destination : result) {
            PaintHelper.paintPoint(robot.getGraphics(), DEBUG_POINT_SIZE, ALL_POTENTIAL_POINTS_COLORS, destination, null);
        }
        return result;
    }

    @Override
    public void runLoop() {
        /**
         * If robot is just stay still for so long, just move it.
         * This case can happens when our robot cannot scan any enemy (but they are still there in the battlefield.
         * They are just outside the range of our radar.
         */
        if (movementContext.isNone() && (robot.getTime() - estimateFinishTime) > 10){
            movementContext.setMoveStrategy(MoveStrategy.RANDOM);
            int direction = 1;
            if (Math.random() < 0.5){
                direction = -1;
            }
            robot.setTurnRight(Math.random()* 360);
            robot.setAhead(direction * 90);
        }
    }

    private static class PointsOnSide {
        private final List<Point2D> sameSide;
        private final List<Point2D> otherSide;

        private PointsOnSide(List<Point2D> sameSide, List<Point2D> otherSide) {
            this.sameSide = sameSide;
            this.otherSide = otherSide;
        }
    }

    private double randomAngleMoveNearlyPerpendicularToEnemy(ScannedRobotEvent scannedRobotEvent) {
        int turnDirection = 1;
        if (Math.random() < .5) {
            turnDirection = -1;
        }
        //Move closer to the enemy.
        double turnAngleToEnemy = 60 + Math.random() * 60;
        double relativeTurnAngleToEnemy = turnAngleToEnemy * turnDirection;
        double absoluteTurnAngleToEnemy = scannedRobotEvent.getBearing() + relativeTurnAngleToEnemy;
        return absoluteTurnAngleToEnemy;
    }

    private double randomAngleMoveFarAwayFromEnemy(ScannedRobotEvent scannedRobotEvent) {
        return randomAngleMoveTowardEnemy(scannedRobotEvent) + 180;
    }

    private double randomAngleMoveTowardEnemy(ScannedRobotEvent scannedRobotEvent) {
        int turnDirection = 1;
        if (Math.random() < .5) {
            turnDirection = -1;
        }
        //Move closer to the enemy.
        double turnAngleToEnemy = 30 + Math.random() * 60;//Don't run too directly to the enemy. At least turn 30 degree from the main direction.
        double relativeTurnAngleToEnemy = turnAngleToEnemy * turnDirection;
        double absoluteTurnAngleToEnemy = scannedRobotEvent.getBearing() + relativeTurnAngleToEnemy;
        return absoluteTurnAngleToEnemy;
    }

    private boolean suspectEnemyHasJustFiredBullet(double oldEnemyEnergy, double currentEnemyEnergy) {
        return oldEnemyEnergy - currentEnemyEnergy <= 3 && oldEnemyEnergy - currentEnemyEnergy >= 0.1;
    }

    private double getOldEnemyEnergy(String enemyName) {
        Optional<Enemy> enemyOptional = getOldEnemy(enemyName);
        if (enemyOptional.isPresent()) {
            return enemyOptional.get().getEnergy();
        } else {
            return RobotPhysics.ROBOT_INITIATE_ENERGY;
        }
    }

    private Optional<Enemy> getOldEnemy(String enemyName) {
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
        EnemyHistory enemyHistory = enemyStatisticContext.getEnemyHistory();
        if (enemyHistory.countHistoryItems() < 2) {
            return Optional.empty();
        } else {
            List<Enemy> recentHistory = enemyHistory.getLatestHistoryItems(2);
            Enemy oldData = recentHistory.get(1);//the second item is the old data. the first item is the current data.
            return Optional.of(oldData);
        }
    }
}
