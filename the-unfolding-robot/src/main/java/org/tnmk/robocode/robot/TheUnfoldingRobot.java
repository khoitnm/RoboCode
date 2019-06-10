package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import org.tnmk.robocode.common.robot.gft.oldalgorithm.GFTAimGun;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class TheUnfoldingRobot extends AdvancedRobot {
    private static int loopIndex = 0;
    private AllEnemiesObservationContext allEnemiesObservationContext = new AllEnemiesObservationContext(this);
    private TheUnfoldingMovement theUnfoldingMovement = new TheUnfoldingMovement(this, allEnemiesObservationContext);
    private TheUnfoldingRadar theUnfoldingRadar = new TheUnfoldingRadar(this, allEnemiesObservationContext);
    private GFTAimGun gftAimGun = new GFTAimGun(this);

    public void run() {
        HiTechDecorator.decorate(this);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        theUnfoldingRadar.initiateRun();
//        while (true) {
//            BotLockRadarHelper.setTurnRadar(radarBotLockContext);
//            execute();
//            loopIndex++;
//        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        theUnfoldingRadar.onScannedRobot(scannedRobotEvent);
        theUnfoldingMovement.onScannedRobot(scannedRobotEvent);
        gftAimGun.onScannedRobot(scannedRobotEvent);
        execute();
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent){
        theUnfoldingRadar.onRobotDeath(robotDeathEvent);
    }

    private void log(String message) {
        LogHelper.logAdvanceRobot(this, loopIndex, message);
    }

}
