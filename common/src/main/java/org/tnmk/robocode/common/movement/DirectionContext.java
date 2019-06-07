package org.tnmk.robocode.common.movement;

import robocode.AdvancedRobot;

/**
 * The context for storing direction.
 */
public class DirectionContext {
    private final AdvancedRobot robot;
    private int direction = 1;

    public DirectionContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void reverseDirection() {
        direction = -direction;
    }

    public AdvancedRobot getRobot() {
        return robot;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
