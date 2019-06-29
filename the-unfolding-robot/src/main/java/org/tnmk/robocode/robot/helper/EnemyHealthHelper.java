package org.tnmk.robocode.robot.helper;

import robocode.Rules;
import robocode.ScannedRobotEvent;

public class EnemyHealthHelper {

    private static final double NEARLY_DIE_ENERGY = Rules.getBulletDamage(Rules.MAX_BULLET_POWER);

    public static boolean isEnemyVeryLowEnergy(ScannedRobotEvent scannedRobotEvent) {
        return scannedRobotEvent.getEnergy() < NEARLY_DIE_ENERGY;
    }

    public static boolean isEnemyHasNoEnergy(ScannedRobotEvent scannedRobotEvent){
        return scannedRobotEvent.getEnergy() < 0.1;
    }
}
