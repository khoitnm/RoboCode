package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.botlock.BotLockRadar;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesScanRadar;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TheUnfoldingRadar implements InitiableRun, LoopableRun, Scannable {
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
    public void runInit() {
        allEnemiesScanRadar.runInit();
    }

    public void runLoop(){
        allEnemiesScanRadar.runLoop();
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        allEnemiesScanRadar.onScannedRobot(scannedRobotEvent);
        if (allEnemiesScanRadar.isFinishInitiateScan360() && allEnemiesObservationContext.countEnemies() <= 1) {
            botLockRadar.onScannedRobot(scannedRobotEvent);
        } else {
            //Do nothing, still continue scanAllEnemies.
        }
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent){
        allEnemiesScanRadar.onRobotDeath(robotDeathEvent);
    }

}
