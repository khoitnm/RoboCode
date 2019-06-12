package org.tnmk.robocode.common.movement;

import java.awt.geom.Point2D;
import robocode.AdvancedRobot;

/**
 * This class help us to know which movement strategy is using.
 */
public class MovementContext  {
    private final AdvancedRobot robot;

    private SpecialMovementType specialMovementType = SpecialMovementType.NONE;
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
     * @return Check if no special movement type
     */
    public boolean isNone() {
        return specialMovementType == null || specialMovementType == SpecialMovementType.NONE;
    }

    /**
     * @param specialMovementType
     * @return check if the current moveType is equals to specialMovementType
     */
    public boolean is(SpecialMovementType specialMovementType) {
        return this.specialMovementType == specialMovementType;
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

    public SpecialMovementType getSpecialMovementType() {
        return specialMovementType;
    }

    public void setSpecialMovementType(SpecialMovementType specialMovementType) {
        this.specialMovementType = specialMovementType;
    }

    public AdvancedRobot getRobot() {
        return robot;
    }

}
