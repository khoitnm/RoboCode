package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.gft.oldalgorithm.GFTAimGun;
import org.tnmk.robocode.common.gun.pattern.CircularPatternGun;
import org.tnmk.robocode.common.gun.pattern.EnemyPatternType;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.OnCustomEventControl;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.CustomEvent;
import robocode.ScannedRobotEvent;

public class TheUnfoldingGun implements InitiableRun, LoopableRun, OnScannedRobotControl, OnCustomEventControl {
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
    private CircularPatternGun circularPatternGun;
    private GunStateContext gunStateContext;


    public TheUnfoldingGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.gunStateContext = new GunStateContext();

        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gftAimGun = new GFTAimGun(robot, gunStateContext);
        this.circularPatternGun = new CircularPatternGun(robot, allEnemiesObservationContext, gunStateContext);
    }

    //TODO share aiming context. When aiming for one algorithm, other algorithm shouldn't change aiming direction.
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        EnemyPatternPrediction enemyPatternPrediction = allEnemiesObservationContext.getEnemyPatternPrediction(scannedRobotEvent.getName());
        if (enemyPatternPrediction == null || !enemyPatternPrediction.isIdentifiedPattern()) {
            aimGftGunIfCloseEnemyEnough(scannedRobotEvent);
        } else {
            EnemyPatternType enemyPatternType = enemyPatternPrediction.getEnemyPatternType();
            if (enemyPatternType == EnemyPatternType.CIRCULAR) {
                circularPatternGun.onScannedRobot(scannedRobotEvent);
            } else {//TODO handle EnemyPatternType.LINEAR
                aimGftGunIfCloseEnemyEnough(scannedRobotEvent);
            }
        }


    }

    private void aimGftGunIfCloseEnemyEnough(ScannedRobotEvent scannedRobotEvent) {
        int totalExistingEnemies = robot.getOthers();
        if (shouldFire(scannedRobotEvent.getDistance(), totalExistingEnemies)) {
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

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        //Nothing at this moment.
    }

    @Override
    public void runInit() {
        //Nothing at this moment.
    }

    @Override
    public void runLoop() {
        circularPatternGun.runLoop();
    }
}
