package org.tnmk.robocode.common.movement.tactic.uturn;

import org.tnmk.robocode.common.movement.ResetableMoveController;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.Rules;

import java.awt.geom.Point2D;

public class NonStopUTurnMoveController implements ResetableMoveController {
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
        double moveAngle = UTurnHelper.reckonMoveAngle(robot, destination);
        double maxVelocity = UTurnHelper.reckonMaxVelocity(moveAngle);
        robot.setMaxVelocity(maxVelocity);
        robot.setTurnRight(moveAngle);
        robot.setAhead(Double.POSITIVE_INFINITY);
        PaintHelper.paintPoint(robot.getGraphics(), 10, HiTechDecorator.FINAL_DESTINATION_COLOR, destination, null);
    }


    @Override
    public void reset() {
        this.destination = null;
        this.robot.setMaxVelocity(Rules.MAX_VELOCITY);
    }
}
