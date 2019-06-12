package org.tnmk.robocode.common.movement.oscillator;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * http://old.robowiki.net/robowiki?Oscillators <br/>
 * <p/>
 * Oscillators usually play perpendicular to enemy bot, and avoid being hit by changing direction frequently. Oscillating movement is very easy to implement, needs only a few lines of code, and can be combined with avoiding and other tricks.<br/>
 * <p/>
 * Some disadvantages of oscillating movement are that you can get easily hit by an advanced targeting system, and that you have little control on your absolute position in the battlefield (you define the position relative to the enemy).<br/>
 */
public class OscillatorHelper {

    /**
     * @param context
     * @param scannedRobotEvent
     * @param moveDistance      how far you want to move (e.g. 185)
     * @param enemyDistance     how far you want to stay away from your target enemy (e.g. 200)
     */
    public static void setMovement(MovementContext context, ScannedRobotEvent scannedRobotEvent, double moveDistance, int enemyDistance) {
        Enemy enemy = EnemyMapper.toEnemy(context.getRobot(), scannedRobotEvent);
        setMovement(context, enemy, moveDistance, enemyDistance);
    }

    public static void setMovement(MovementContext context, Enemy enemy, double moveDistance, int enemyDistance) {
        //** it is from onScannedRobot
        AdvancedRobot robot = context.getRobot();
        if (DoubleUtils.isConsideredZero(robot.getDistanceRemaining())) {
            context.reverseDirection();
            robot.setAhead(moveDistance * context.getDirection());
        }
        robot.setTurnRightRadians(enemy.getBearingRadians() + Math.PI / 2 - 0.5236 * context.getDirection() * (enemy.getDistance() > enemyDistance ? 1 : -1));
    }
}
