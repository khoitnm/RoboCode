package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.gft.oldalgorithm.GFTAimGun;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class TheUnfoldingGun {
    /**
     * The furthest distance which we should fire on target in one-on-one fights.
     * Note: this distance should never be lower than {@link TheUnfoldingMovement#IDEAL_ENEMY_OSCILLATOR_DISTANCE}
     */
    public static final double FURTHEST_DISTANCE_TO_FIRE_ONE_ON_ONE = 300;
    /**
     * For each additional enemies, increase the fire distance by this percentage.
     */
    private static final double PERCENTAGE_INCREASING_DISTANCE_FOR_EACH_ADDITIONAL_ENEMY = 0.25;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private GFTAimGun gftAimGun;


    public TheUnfoldingGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gftAimGun = new GFTAimGun(robot);
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        if (shouldFire(scannedRobotEvent.getDistance(), allEnemiesObservationContext.countEnemies())) {
            gftAimGun.onScannedRobot(scannedRobotEvent);
        }
    }

    /**
     * When the distance is too far, don't fire.
     * However, if there's so many enemies, fire bullets even if the distance is far because there could be a change we hit other enemies accidentally.
     *
     * @param aimedEnemyDistance the current distance between our robot and a target enemy.
     * @param enemiesCount       the total of remain enemies.
     * @return
     */
    private boolean shouldFire(double aimedEnemyDistance, int enemiesCount) {
        double totalIncreasePercentageDistance = enemiesCount * PERCENTAGE_INCREASING_DISTANCE_FOR_EACH_ADDITIONAL_ENEMY;
        boolean result = aimedEnemyDistance <= FURTHEST_DISTANCE_TO_FIRE_ONE_ON_ONE * (1 + totalIncreasePercentageDistance);
        return result;
    }
}
