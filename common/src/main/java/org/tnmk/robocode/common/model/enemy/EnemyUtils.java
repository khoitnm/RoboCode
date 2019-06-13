package org.tnmk.robocode.common.model.enemy;

import robocode.Rules;

public class EnemyUtils {
    private static final double STILL_NEW_TIME_PERIOD = 360 / Rules.RADAR_TURN_RATE;

    /**
     * @param enemy
     * @param currentTime
     * @return This method check whether the information of an enemy is still "new" (within {@link #STILL_NEW_TIME_PERIOD} ticks) or not.
     */
    public static boolean isEnemyNew(Enemy enemy, double currentTime) {
        return currentTime - enemy.getTime() <= STILL_NEW_TIME_PERIOD;
    }
}
