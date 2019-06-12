package org.tnmk.robocode.common.movement.backandforth;

import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
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

    /**
     * @deprecated This code will show why reverse direction by changing newHeading doesn't work.
     * <p/>
     * Read the following data and you will understand:<br/>
     * heading: 8.062585080431573, direction: 1, newHeading: -171.93741491956843<br/>
     * heading: 358.0625850804316, direction: 1, newHeading: 178.06258508043152<br/>
     * heading: 8.062585080431571, direction: 1, newHeading: -171.93741491956843<br/>
     * heading: 358.0625850804316, direction: 1, newHeading: 178.06258508043152<br/>
     * heading: 8.062585080431571, direction: 1, newHeading: -171.93741491956843<br/>
     * heading: 358.0625850804316, direction: 1, newHeading: 178.06258508043152<br/>
     * heading: 18.06258508043157, direction: 1, newHeading: -161.93741491956845<br/>
     * heading: 8.062585080431573, direction: 1, newHeading: -171.93741491956843<br/>
     * heading: 358.0625850804316, direction: 1, newHeading: 178.06258508043152<br/>
     * heading: 8.062585080431571, direction: 1, newHeading: -171.93741491956843<br/>
     *
     * @param robot
     * @param distance
     */
    @Deprecated
    public static void setMovement(AdvancedRobot robot, double distance) {
        if (DoubleUtils.isConsideredZero(robot.getDistanceRemaining())) {
            double headingRadian = robot.getHeadingRadians();
            double newHeadingRadian = AngleUtils.reverseRadian(headingRadian);
            int direction = MathUtils.sign(Math.cos(headingRadian));
            int newDirection = -direction;
            robot.setAhead(newDirection * distance);
            robot.setTurnRightRadians(newHeadingRadian);
            robot.out.println(
                    "heading: " + robot.getHeading()
                            + "\n\t, direction: " + direction
                            + "\n\t, newHeading: " + AngleUtils.toDegree(newHeadingRadian)
                            + "\n\t, velocity: " + robot.getVelocity()
                            + "\n\t, distanceRemain: " + robot.getDistanceRemaining()
                            + "\n\t, width: " + robot.getWidth()
            );
        }
    }
}
