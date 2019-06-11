package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.constant.RobotPhysics;

public class EnemyHelper {
    private static final double STILL_NEW_TIME_PERIOD = 360 / RobotPhysics.RADAR_TURN_VELOCITY;

    /**
     * @param enemy
     * @param currentTime
     * @return This method check whether the information of an enemy is still "new" (within {@link #STILL_NEW_TIME_PERIOD} ticks) or not.
     */
    public static boolean isEnemyNew(Enemy enemy, double currentTime) {
        return currentTime - enemy.getTime() <= STILL_NEW_TIME_PERIOD;
    }
}
