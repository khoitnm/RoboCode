package org.tnmk.robocode.common.movement.uturn;

import java.awt.geom.Point2D;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.movement.MoveTactic;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.ResetableMoveController;
import org.tnmk.robocode.common.robot.LoopableRun;
import robocode.AdvancedRobot;
import robocode.Rules;

public class UTurnMoveController implements ResetableMoveController, LoopableRun {
    /**
     * the maximum of ticks for applying this UTurnMoveController.
     */
    private static final long MAX_RUN_TICKS = 20;
    private static final double MIN_MAX_VELOCITY = 2.0d;
    private static final double MAX_MAX_VELOCITY = Rules.MAX_VELOCITY;

    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    private long startTime = Long.MIN_VALUE;
    private Point2D destination = null;

    public UTurnMoveController(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }

    public void setMoveToDestination(AdvancedRobot robot, Point2D destination) {
        movementContext.setMoveTactic(MoveTactic.QUICK_UTURN);
        startTime = robot.getTime();
        this.destination = destination;
        Point2D currentPosition = new Point2D.Double(robot.getX(), robot.getY());
        double distance = currentPosition.distance(destination);
        double moveAngle = GeoMathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition.getX(), currentPosition.getY(), destination.getX(), destination.getY());
        robot.setMaxVelocity(reckonMaxVelocity(moveAngle));
        robot.setTurnRight(moveAngle);
        robot.setAhead(distance);//if you want it to stop at the destination, use setAhead(distance to destination + some additional distance for turning direction)
    }

    @Override
    public void runLoop() {
        if (movementContext.getMoveTactic() != MoveTactic.QUICK_UTURN) {
            return;
        }
        double remainDistance = getRemainDistance();
        if (DoubleUtils.isConsideredZero(remainDistance) || robot.getTime() - startTime >= MAX_RUN_TICKS) {
            stopMovementTactic();
        }else{
            double maxVelocity = reckonMaxVelocity(robot.getTurnRemaining());
            robot.setMaxVelocity(maxVelocity);
            robot.setAhead(remainDistance);
        }
    }

    @Override
    public void reset() {
        stopMovementTactic();
    }

    private void stopMovementTactic(){
        this.destination = null;
        this.startTime = Long.MIN_VALUE;
        this.robot.setMaxVelocity(Rules.MAX_VELOCITY);
        this.movementContext.setMoveTactic(MoveTactic.NONE);
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
        return distanceToDestination;
    }
}
