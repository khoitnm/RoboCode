package org.tnmk.robocode.common.movement.tactic.uturn;

import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.ResetableMoveController;
import org.tnmk.robocode.common.robot.LoopableRun;
import robocode.AdvancedRobot;
import robocode.Rules;
import sun.rmi.runtime.Log;

import java.awt.geom.Point2D;

public class UTurnMoveController implements ResetableMoveController, LoopableRun {
    private static final double MIN_MAX_VELOCITY = 2.0d;
    private static final double MAX_MAX_VELOCITY = Rules.MAX_VELOCITY;

    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    private Point2D destination = null;

    public UTurnMoveController(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }

    /**
     * Trigger the UTurn Movement
     *
     * @param robot
     * @param destination
     */
    public void setMoveToDestination(AdvancedRobot robot, Point2D destination) {
        this.destination = destination;
        Point2D currentPosition = new Point2D.Double(robot.getX(), robot.getY());
        double distance = currentPosition.distance(destination);
        double moveAngle = GeoMathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition.getX(), currentPosition.getY(), destination.getX(), destination.getY());
        robot.setMaxVelocity(reckonMaxVelocity(moveAngle));
        robot.setTurnRight(moveAngle);
        robot.setAhead(distance);//if you want it to reset at the destination, use setAhead(distance to destination + some additional distance for turning direction)
        LogHelper.logRobotMovement(robot, "move to destination: moveAhead: " + distance);
    }

    @Override
    public void runLoop() {
        if (isStopped()) {
            /** Just keep it's remain stopped
             *  It'll be triggered by other Controller (by executing {@link #setMoveToDestination(AdvancedRobot, Point2D)})
             */
            return;
        }

        double remainDistance = getRemainDistance();
        if (DoubleUtils.isConsideredZero(remainDistance)) {
            reset();
        } else {
            setMoveToDestination(robot, destination);
        }
    }

    @Override
    public void reset() {
        this.destination = null;
        this.robot.setMaxVelocity(Rules.MAX_VELOCITY);
        LogHelper.logRobotMovement(robot, "Reset UTurnMovement");
    }

    public boolean isStopped() {
        return this.destination == null || DoubleUtils.isConsideredZero(getRemainDistance());
    }

    private double reckonMaxVelocity(double remainTurnAngleDegree) {
        double maxVelocity;
        if (remainTurnAngleDegree > 30) {
            maxVelocity = MIN_MAX_VELOCITY;
        } else {
            maxVelocity = MAX_MAX_VELOCITY - (remainTurnAngleDegree / 10) * 2;
        }
        return maxVelocity;
    }

    private double getRemainDistance() {
        Point2D robotPosition = BattleFieldUtils.constructRobotPosition(robot);
        double distanceToDestination = robotPosition.distance(destination);
        LogHelper.logRobotMovement(robot, "Calculated remainDistance: "+distanceToDestination);
        return distanceToDestination;
    }
}
