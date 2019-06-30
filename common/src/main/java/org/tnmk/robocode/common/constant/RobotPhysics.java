package org.tnmk.robocode.common.constant;

import robocode.Robot;
import robocode.Rules;

/**
 * View more at {@link Rules}
 */
public class RobotPhysics {
    /**
     * speed of radar (degree)
     */
    public static final int RADAR_TURN_VELOCITY = 45;
    /**
     * speed of turning gun (degree)
     */
    public static final double GUN_TURN_VELOCITY = 20;

    public static final double ROBOT_MAX_VELOCITY = 8;
    /**
     * When stopping, robot's velocity will reduce {@link #ROBOT_DECCELERATION} per tick.
     */
    public static final double ROBOT_DECCELERATION = 2;
    /**
     * When starting running, robot's velocity will increase {@link #ROBOT_ACCELERATION} per tick.
     */
    public static final double ROBOT_ACCELERATION = 1;
    /**
     * The full size of a robot.
     * Note: if you look at {@link Robot#getWidth()}, the value is 36
     */
    public static final double ROBOT_SIZE = 50;

    /**
     * velocity from 8 -> 6 -> 4 -> 2 -> 0: 5 ticks
     */
    public static final double ROBOT_TICKS_TO_STOP_FROM_FULL_SPEED = 5;
    /**
     * When a robot at full speed, the distance for it to reset is 20
     * velocity from 8 -> 6 -> 4 -> 2 -> 0: distance 20
     */
    public static final double ROBOT_DISTANCE_TO_STOP_FROM_FULL_SPEED = 20;
    /**
     * The energy of each robot when starting a round (match)
     */
    public static final double ROBOT_INITIATE_ENERGY = 100;
}
