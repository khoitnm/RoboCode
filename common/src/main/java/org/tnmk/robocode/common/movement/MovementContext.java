package org.tnmk.robocode.common.movement;

import java.awt.geom.Point2D;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.log.LogHelper;
import robocode.AdvancedRobot;
import robocode.StatusEvent;

/**
 * The context to manage {@link MoveStrategyType}.<br/>
 * This class help us to know which movement strategy is using.
 * <p/>
 * This object should be managed by some composition movement class such as TheUnfoldingMovement.
 */
public class MovementContext {
    private final AdvancedRobot robot;
    private MoveStrategyType moveStrategyType = MoveStrategyType.NONE;
    private MoveTactic moveTactic = MoveTactic.NONE;
    private Movement movement = null;
    /**
     * This value should be constantly updated every tick by {@link robocode.Robot#onStatus(StatusEvent)}.
     * <p/>
     * Note: We may not need this,
     * check {@link Move2DUtils#setMoveToDestinationWithShortestPath(AdvancedRobot, Point2D)} to see how can it change direction without knowing direction.
     * (hint: by changing heading angle)???
     * <br/>
     * <p/>
     * I tried but it didn't work?! So we still need it. And actually, we don't need to manually update direction.<br/>
     * Just set {@link robocode.Robot#ahead(double)}, and then we can setup {@link robocode.Robot#onStatus(StatusEvent)} to automatically updated direction.
     */
    private int direction = 1;

    public MovementContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    /**
     * Set moveStrategyType is {@link MoveStrategyType#NONE}
     */
    public void setNone() {
        /** Don't directly set strategy to NONE because we may already has some debugging statements inside {@link #setMoveStrategyType(MoveStrategyType)} */
        setMoveStrategyType(MoveStrategyType.NONE);
    }

    /**
     * @return Check if no special movement type
     */
    public boolean isNone() {
        return moveStrategyType == null || moveStrategyType == MoveStrategyType.NONE;
    }

    /**
     * @param moveStrategyType
     * @return check if the current moveType is equals to moveStrategyType
     */
    public boolean is(MoveStrategyType moveStrategyType) {
        return this.moveStrategyType == moveStrategyType;
    }

    public void reverseDirection() {
        direction = -direction;
    }

    public int getDirection() {
        return direction;
    }


    public void setDirection(int direction) {
        if (direction == 0) {
            throw new IllegalArgumentException("Direction cannot be 0");
        }
        if (Math.abs(direction) != 1) {
            this.direction = direction / (Math.abs(direction));
        } else {
            this.direction = direction;
        }
    }

    public MoveStrategyType getMoveStrategyType() {
        return moveStrategyType;
    }

    public void setMoveStrategyType(MoveStrategyType moveStrategyType) {
        if (DebugHelper.isDebugMoveStrategyChange()) {
            LogHelper.logRobotMovement(robot, this.moveStrategyType + ": end");//end the old strategy
            LogHelper.logRobotMovement(robot, moveStrategyType + ": begin");//begin the new strategy
        }
        this.moveStrategyType = moveStrategyType;
    }

    public AdvancedRobot getRobot() {
        return robot;
    }

    public boolean isAmong(MoveStrategyType... moveStrategies) {
        for (MoveStrategyType strategy : moveStrategies) {
            if (strategy == this.moveStrategyType) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotAmong(MoveStrategyType... moveStrategies) {
        return !isAmong(moveStrategies);
    }

    public boolean hasLowerPriority(MoveStrategyType moveStrategyType) {
        return this.moveStrategyType.getPriorty() < moveStrategyType.getPriorty();
    }

    public boolean hasLowerOrEqualPriority(MoveStrategyType moveStrategyType) {
        return this.moveStrategyType.getPriorty() <= moveStrategyType.getPriorty();
    }

    public boolean hasLowerOrEqualPriorityButDifferentStrategy(MoveStrategyType moveStrategyType) {
        boolean result = hasLowerOrEqualPriority(moveStrategyType) && moveStrategyType != this.moveStrategyType;
        return result;
    }

    public MoveTactic getMoveTactic() {
        return moveTactic;
    }

    public void setMoveTactic(MoveTactic moveTactic) {
        this.moveTactic = moveTactic;
    }
}
