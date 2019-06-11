package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.botlock.BotLockRadar;
import org.tnmk.robocode.common.radar.optimalscan.OptimalScanRadar;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.CustomableEvent;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.CustomEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TheUnfoldingRadar implements Scannable, RobotDeathTrackable, InitiableRun, CustomableEvent {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private final BotLockRadar botLockRadar;
    private OptimalScanRadar allEnemiesScanRadar;

    public TheUnfoldingRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        this.botLockRadar = new BotLockRadar(robot);
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

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        allEnemiesScanRadar.onScannedRobot(scannedRobotEvent);
        int totalExistingEnemies = robot.getOthers();
        if (allEnemiesScanRadar.isScannedAllEnemiesAtLeastOnce() && totalExistingEnemies <= 1) {
            botLockRadar.onScannedRobot(scannedRobotEvent);
        } else {
            //Do nothing, still continue scanAllEnemies.
        }
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
