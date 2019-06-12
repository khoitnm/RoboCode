package org.tnmk.robocode.common.movement.backandforth;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.AdvancedRobot;

/**
 * http://old.robowiki.net/robowiki?Oscillators
 * Oscillators usually play perpendicular to enemy bot, and avoid being hit by changing direction frequently. Oscillating movement is very easy to implement, needs only a few lines of code, and can be combined with avoiding and other tricks.
 * Some disadvantages of oscillating movement are that you can get easily hit by an advanced targeting system, and that you have little control on your absolute position in the battlefield (you define the position relative to the enemy).
 */
public class BackAndForthHelper {

    public static void setMovement(MovementContext context, int distance) {
        AdvancedRobot robot = context.getRobot();
        if (DoubleUtils.isConsideredZero(robot.getVelocity())) {
            context.reverseDirection();
            robot.setAhead(distance * context.getDirection());
        }
    }
}
