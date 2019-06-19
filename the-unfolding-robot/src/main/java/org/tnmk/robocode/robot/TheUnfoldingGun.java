package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.blackpearl.BlackPearlGun;
import org.tnmk.robocode.common.gun.briareos.BriareosGun;
import org.tnmk.robocode.common.gun.gft.oldalgorithm.GFTAimGun;
import org.tnmk.robocode.common.gun.mobius.MobiusGun;
import org.tnmk.robocode.common.gun.pattern.PatternPredictionGun;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.*;
import robocode.*;

public class TheUnfoldingGun implements InitiableRun, LoopableRun, OnScannedRobotControl, OnCustomEventControl, OnHitBulletControl, OnWinControl {
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

    private final BriareosGun briareosGun;
    private final GFTAimGun gftAimGun;
    private final PatternPredictionGun patternPredictionGun;
    private final MobiusGun mobiusGun;
    private final BlackPearlGun blackPearlGun;

    private final GunStateContext gunStateContext;


    public TheUnfoldingGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = new GunStateContext();

        this.briareosGun = new BriareosGun(robot);
        this.mobiusGun = new MobiusGun(robot);
        this.gftAimGun = new GFTAimGun(robot, gunStateContext);
        this.blackPearlGun = new BlackPearlGun(robot, gunStateContext);
        this.patternPredictionGun = new PatternPredictionGun(robot, allEnemiesObservationContext, gunStateContext);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(scannedRobotEvent.getName());
//        LogHelper.logSimple(robot, "Enemy: " + scannedRobotEvent.getName()
//                + "\n\t\t Pattern: " + enemyStatisticContext.getPatternIdentification()
//                + "\n\t\t predictionHistory: \t" + enemyStatisticContext.getEnemyPredictionHistory().getAllHistoryItems()
//        );
        if (enemyStatisticContext != null && enemyStatisticContext.hasCertainPattern()) {
            patternPredictionGun.onScannedRobot(scannedRobotEvent);
        } else {
            aimGFTGunWhenPropriate(scannedRobotEvent);
//            blackPearlGun.onScannedRobot(scannedRobotEvent);
//            mobiusGun.onScannedRobot(scannedRobotEvent);
//            briareosGun.onScannedRobot(scannedRobotEvent);
        }
    }

    private void aimGFTGunWhenPropriate(ScannedRobotEvent scannedRobotEvent) {
        if (shouldApplyGFTGun(scannedRobotEvent.getDistance(), robot.getOthers())) {
            gftAimGun.onScannedRobot(scannedRobotEvent);
//            robot.setBodyColor(HiTechDecorator.ROBOT_BORDY_COLOR);
        } else {
//            robot.setBodyColor(Color.RED);
            /** Don't fire, both GFT and MoebiusGun work badly in this case*/
        }
    }

    /**
     * When the distance is too far, don't fire.
     * However, if there's so many enemies, fire bullets even if the distance is far because there could be a change we hit other enemies accidentally.
     * <p/>
     * NOTE: Actually, without this condition, GFTGun still works, but it works very badly.
     *
     * @param aimedEnemyDistance the current distance between our robot and a target enemy.
     * @param enemiesCount       the total of remain enemies.
     * @return
     */
    private boolean shouldApplyGFTGun(double aimedEnemyDistance, int enemiesCount) {
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
//        blackPearlGun.runInit();
//        briareosGun.runInit();
        //Nothing at this moment.
    }

    @Override
    public void runLoop() {
//        briareosGun.runLoop();
        patternPredictionGun.runLoop();
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        //FIXME There's no way to make sure the current strategy is still the same at this moment.
//        if (gunStateContext.isStrategy(GunStrategy.BLACK_PEARL)) {
//            blackPearlGun.onHitByBullet(hitByBulletEvent);
//        }
    }

    @Override
    public void onWin(WinEvent winEvent) {
//        blackPearlGun.onWin(winEvent);
    }
}
