package org.tnmk.robocode.common.movement;

import java.awt.geom.Point2D;
import robocode.AdvancedRobot;
import robocode.StatusEvent;

/**
 * The context to manage {@link MoveStrategy}.<br/>
 * This class help us to know which movement strategy is using.
 * <p/>
 * This object should be managed by some composition movement class such as TheUnfoldingMovement.
 */
public class MovementContext {
    private final AdvancedRobot robot;

    private MoveStrategy moveStrategy = MoveStrategy.NONE;


    /**
     * This value should be constantly updated every tick by {@link robocode.Robot#onStatus(StatusEvent)}.
     * <p/>
     * Note: We may not need this,
     * check {@link org.tnmk.robocode.common.helper.Move2DHelper#setMoveToDestinationWithShortestPath(AdvancedRobot, Point2D)} to see how can it change direction without knowing direction.
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
     * Set moveStrategy is {@link MoveStrategy#NONE}
     */
    public void setNone() {
        this.moveStrategy = MoveStrategy.NONE;
    }

    /**
     * @return Check if no special movement type
     */
    public boolean isNone() {
        return moveStrategy == null || moveStrategy == MoveStrategy.NONE;
    }

    /**
     * @param moveStrategy
     * @return check if the current moveType is equals to moveStrategy
     */
    public boolean is(MoveStrategy moveStrategy) {
        return this.moveStrategy == moveStrategy;
    }

    public void reverseDirection() {
        direction = -direction;
    }

    public int getDirection() {
        return direction;
    }


    public void setDirection(int direction) {
        this.direction = direction;
    }

    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }

    public void setMoveStrategy(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
    }

    public AdvancedRobot getRobot() {
        return robot;
    }

}
