package org.tnmk.robocode.common.movement.tactic.uturn;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.movement.ResetableMoveController;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.Rules;

import java.awt.geom.Point2D;

public class NonStopUTurnMoveController implements ResetableMoveController, LoopableRun {
    private final AdvancedRobot robot;

    private Point2D destination = null;

    public NonStopUTurnMoveController(AdvancedRobot robot) {
        this.robot = robot;
    }

    /**
     * Trigger the UTurn Movement
     *
     * @param robot
     * @param destination
     */
    public void setMoveToDestination(AdvancedRobot robot, Point2D destination) {
        this.destination = destination;
        double moveAngle = Move2DUtils.reckonMoveAngleDegree(robot, destination);
        double maxVelocity = UTurnHelper.reckonMaxVelocity(moveAngle);
        robot.setMaxVelocity(maxVelocity);
        robot.setTurnRight(moveAngle);
        robot.setAhead(Double.POSITIVE_INFINITY);
        PaintHelper.paintPoint(robot.getGraphics(), 10, HiTechDecorator.FINAL_DESTINATION_COLOR, destination, null);
    }

    @Override
    public void runLoop() {
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
    }
}
