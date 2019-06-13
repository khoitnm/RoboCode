package org.tnmk.robocode.common.movement;

import java.awt.geom.Point2D;
import robocode.AdvancedRobot;

/**
 * The context to manage {@link MoveStrategy}.<br/>
 * This class help us to know which movement strategy is using.
 */
public class MovementContext  {
    private final AdvancedRobot robot;

    private MoveStrategy moveStrategy = MoveStrategy.NONE;
    /**
     * @deprecated We may not need this,
     * check {@link org.tnmk.robocode.common.helper.Move2DHelper#setMoveToDestinationWithShortestPath(AdvancedRobot, Point2D)} to see how can it change direction without knowing direction.
     * (hint: by changing heading angle).
     */
    @Deprecated
    private int direction = 1;

    public MovementContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    /**
     * Set moveStrategy is {@link MoveStrategy#NONE}
     */
    public void setNone(){
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

    /**
     * @deprecated View {@link #direction}
     */
    @Deprecated
    public void reverseDirection() {
        direction = -direction;
    }

    /**
     * @see #direction
     */
    @Deprecated
    public int getDirection() {
        return direction;
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
