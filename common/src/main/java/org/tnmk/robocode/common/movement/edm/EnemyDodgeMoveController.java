package org.tnmk.robocode.common.movement.edm;

import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.movement.MoveController;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

public class EnemyDodgeMoveController implements MoveController, InitiableRun, OnScannedRobotControl {
    private final AdvancedRobot robot;
    private final EDMHelper edmHelper;
    /**
     * Data of this context is gathered by some radar logic
     */
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    /**
     * @param robot
     * @param allEnemiesObservationContext data inside here should be gather by some radar logic.
     */
    public EnemyDodgeMoveController(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        this.edmHelper = new EDMHelper(robot);
    }

    public void runInit() {
        this.edmHelper.runInit();
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Collection<Point2D> enemiesPositionsPoint2Ds = getEnemiesPositionsFromContext();
        EDMHelper.DestinationCalculation destinationCalculation = edmHelper.getDestination(enemiesPositionsPoint2Ds);
        Point2D.Double destinationPosition2D = destinationCalculation.getDestination();

        moveToDestination(destinationPosition2D);
        edmHelper.paintEnemiesAndDestination(this.robot, destinationCalculation.getEnemies(), destinationPosition2D);
    }

    private Collection<Point2D> getEnemiesPositionsFromContext() {
        Collection<Point2D> enemiesPositions = allEnemiesObservationContext.getEnemiesMapByName().values()
                .stream()
                .map(enemy -> enemy.getPosition())
                .collect(Collectors.toList());
        return enemiesPositions;
    }

    /**
     * TODO The current code works, but sometimes it choose the long way to go to the destination (by chossing the opposited direction).
     * We can improve to make it choose the right direction and go to the target quicker.
     *
     * @param destinationPosition2D
     */
    private void moveToDestination(Point2D destinationPosition2D) {
        Move2DUtils.setMoveToDestinationWithShortestPath(robot, destinationPosition2D);

//        Point2D currentPosition2D = new Point2D.Double(robot.getX(), robot.getY());
//
//        Point currentPosition = PointConverter.toPoint(currentPosition2D);
//        Point destinationPosition = PointConverter.toPoint(destinationPosition2D);
//
//        double moveAngle = GeoMathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition, destinationPosition);
//        robot.setTurnRight(moveAngle);
//        robot.setAhead(Double.POSITIVE_INFINITY);
    }

}
