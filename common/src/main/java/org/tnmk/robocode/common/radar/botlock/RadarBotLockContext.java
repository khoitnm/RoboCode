package org.tnmk.robocode.common.radar.botlock;

import robocode.AdvancedRobot;

public class RadarBotLockContext {
    private final AdvancedRobot robot;
    /**
     * How many ticks since the last time the robot see its target.
     */
    private int timeSinceLastSeenEnemy = 0;

    private double enemyAbsoluteBearing;

    public RadarBotLockContext(AdvancedRobot robot) {
        this.robot = robot;
    }

    public int getTimeSinceLastSeenEnemy() {
        return timeSinceLastSeenEnemy;
    }

    public void setTimeSinceLastSeenEnemy(int timeSinceLastSeenEnemy) {
        this.timeSinceLastSeenEnemy = timeSinceLastSeenEnemy;
    }

    public double getEnemyAbsoluteBearing() {
        return enemyAbsoluteBearing;
    }

    public void setEnemyAbsoluteBearing(double enemyAbsoluteBearing) {
        this.enemyAbsoluteBearing = enemyAbsoluteBearing;
    }

    public AdvancedRobot getRobot() {
        return robot;
    }
}
