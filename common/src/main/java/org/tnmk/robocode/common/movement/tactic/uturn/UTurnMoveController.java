package org.tnmk.robocode.common.movement.tactic.uturn;

import java.awt.geom.Point2D;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.ResetableMoveController;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.Rules;

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
        double moveAngle = Move2DUtils.reckonMoveAngleDegree(robot, destination);
        double maxVelocity = UTurnHelper.reckonMaxVelocity(moveAngle);
        robot.setMaxVelocity(maxVelocity);
        robot.setTurnRight(moveAngle);
        robot.setAhead(distance);//if you want it to reset at the destination, use setAhead(distance to destination + some additional distance for turning direction)
        PaintHelper.paintPoint(robot.getGraphics(), 10, HiTechDecorator.FINAL_DESTINATION_COLOR, destination, null);
//        LogHelper.logRobotMovement(robot, "move to destination: moveAhead: " + distance + ", maxVelocity: " + maxVelocity);
    }

    @Override
    public void runLoop() {
        if (isStopped()) {
            /** Just keep it's remain stopped
             *  It'll be triggered by other Controller (by executing {@link #setMoveToDestination(AdvancedRobot, Point2D)})
             */
            return;
        }

        double remainDistance = Move2DUtils.reckonRemainDistanceToDestination(this.robot, this.destination);
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
//        LogHelper.logRobotMovement(robot, "Reset UTurnMovement");
    }

    public boolean isStopped() {
        double remainDistance = Move2DUtils.reckonRemainDistanceToDestination(this.robot, this.destination);
        return this.destination == null || DoubleUtils.isConsideredZero(remainDistance);
    }


}
