package org.tnmk.robocode.common.movement.edm;

import org.tnmk.common.converter.PointConverter;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

public class EnemyDodgeMovement {
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
    public EnemyDodgeMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        this.edmHelper = new EDMHelper(robot);
    }

    public void initiateRun(){
        this.edmHelper.initiateRun();
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
    private void moveToDestination(Point2D.Double destinationPosition2D) {
        Point2D.Double currentPosition2D = new Point2D.Double(robot.getX(), robot.getY());
        Point currentPosition = PointConverter.toPoint(currentPosition2D);
        Point destinationPosition = PointConverter.toPoint(destinationPosition2D);

        double moveAngle = MathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition, destinationPosition);
        robot.setTurnRight(moveAngle);
        robot.setAhead(Double.POSITIVE_INFINITY);
    }

}
