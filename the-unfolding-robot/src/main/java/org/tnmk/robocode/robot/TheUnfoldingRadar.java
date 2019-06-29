package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.botlock.BotLockRadar;
import org.tnmk.robocode.common.radar.optimalscan.OptimalScanRadar;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.OnCustomEventControl;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.OnRobotDeathControl;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.robot.helper.EnemyHealthHelper;
import robocode.AdvancedRobot;
import robocode.CustomEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TheUnfoldingRadar implements OnScannedRobotControl, OnRobotDeathControl, InitiableRun, OnCustomEventControl {
    private static final double ENEMY_CLOSE_DISTANCE = 400;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private final BotLockRadar botLockRadar;
    private OptimalScanRadar allEnemiesScanRadar;

    public TheUnfoldingRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        this.botLockRadar = new BotLockRadar(robot, allEnemiesObservationContext);
        this.allEnemiesScanRadar = new OptimalScanRadar(robot, allEnemiesObservationContext);
    }

//    @Override
//    public void runLoop() {
//        allEnemiesScanRadar.runLoop();
//    }

    @Override
    public void runInit() {
        allEnemiesScanRadar.runInit();
    }

    /**
     * General strategy:<br/>
     * If there's an enemy close to our robot, lock radar on it. After awhile, scan other robots again.
     * </p>
     * This way also help the Anti-Gravity destination doesn't change so quickly.<br/>
     * The reason is when our robot lock radar to single one target, our data about other enemies still the same (old), so the anti-gravity calculation doesn't change much.<br/>
     * Hence the destination will mostly the same.
     *
     * @param scannedRobotEvent
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        if (isCloseEnemy(scannedRobotEvent)) {
            if (allEnemiesScanRadar.isScannedAllEnemiesAtLeastOnce() && allEnemiesObservationContext.isAllEnemiesHasNewData()) {
                botLockRadar.onScannedRobot(scannedRobotEvent);
            } else {
                allEnemiesScanRadar.onScannedRobot(scannedRobotEvent);
            }
        } else if (EnemyHealthHelper.isEnemyVeryLowEnergy(scannedRobotEvent)) {
            botLockRadar.onScannedRobot(scannedRobotEvent);
        } else {
            int totalExistingEnemies = robot.getOthers();
            if (allEnemiesScanRadar.isScannedAllEnemiesAtLeastOnce() && totalExistingEnemies <= 1) {
                botLockRadar.onScannedRobot(scannedRobotEvent);
            } else {
                allEnemiesScanRadar.onScannedRobot(scannedRobotEvent);
                //Do nothing, still continue scanAllEnemies.
            }
        }
    }

    private boolean isCloseEnemy(ScannedRobotEvent scannedRobotEvent) {
        double distance = scannedRobotEvent.getDistance();
        return distance < ENEMY_CLOSE_DISTANCE;
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        allEnemiesScanRadar.onRobotDeath(robotDeathEvent);
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        allEnemiesScanRadar.onCustomEvent(customEvent);
    }
}
