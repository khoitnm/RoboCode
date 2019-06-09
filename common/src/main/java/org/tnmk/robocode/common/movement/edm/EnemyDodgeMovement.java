package org.tnmk.robocode.common.movement.edm;

import org.tnmk.common.converter.PointConverter;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.helper.MoveHelper;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.util.*;

public class EnemyDodgeMovement {
    private final AdvancedRobot robot;


    private final EDMHelper edmHelper;
    private final Map<String, Point2D.Double> enemiesPositions = Collections.synchronizedMap(new HashMap<>());

    public EnemyDodgeMovement(AdvancedRobot robot) {
        this.robot = robot;
        this.edmHelper = new EDMHelper(robot);
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Point targetPosition = MoveHelper.reckonTargetPosition(robot, scannedRobotEvent);
        Point2D.Double targetPositionPoint2D = new Point2D.Double(targetPosition.getX(), targetPosition.getY());
        enemiesPositions.put(scannedRobotEvent.getName(), targetPositionPoint2D);

        Point2D.Double currentPosition2D = new Point2D.Double(robot.getX(), robot.getY());
        Collection<Point2D.Double> enemiesPositionsPoint2Ds = enemiesPositions.values();
        Point2D.Double destinationPosition2D = edmHelper.getDestination(enemiesPositionsPoint2Ds);
        Point currentPosition = PointConverter.toPoint(currentPosition2D);
        Point destinationPosition = PointConverter.toPoint(destinationPosition2D);

        double moveAngle = MathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition, destinationPosition);
        robot.setTurnRight(moveAngle);
//        double distance = currentPosition2D.distance(destinationPosition2D);
        robot.setAhead(Double.POSITIVE_INFINITY);

        edmHelper.paintEnemiesAndDestination(this.robot, enemiesPositionsPoint2Ds, destinationPosition2D);
    }


    public EDMHelper getEdmHelper() {
        return edmHelper;
    }
}
