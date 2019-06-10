package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.botlock.BotLockRadar;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesScanRadar;
import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TheUnfoldingRadar {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private final BotLockRadar botLockRadar;
    private AllEnemiesScanRadar allEnemiesScanRadar;

    public TheUnfoldingRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        this.botLockRadar = new BotLockRadar(robot);
        this.allEnemiesScanRadar = new AllEnemiesScanRadar(robot, allEnemiesObservationContext);
    }

    /**
     * This method should be trigger in the beginning of {@link Robot#run()}, but not in the while-loop block.
     */
    public void initiateRun() {
        allEnemiesScanRadar.initiateRun();
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        allEnemiesScanRadar.onScannedRobot(scannedRobotEvent);
        if (allEnemiesScanRadar.isFinishScanInitiate360() && allEnemiesObservationContext.countEnemies() <= 1) {
            botLockRadar.onScannedRobot(scannedRobotEvent);
        } else {
            //Do nothing, still continue scanAllEnemies.
        }
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent){
        allEnemiesScanRadar.onRobotDeath(robotDeathEvent);
    }

}
