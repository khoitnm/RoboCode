package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.antigravity.DustBunnyAntiGravityMovement;
import org.tnmk.robocode.common.movement.edm.EnemyDodgeMovement;
import org.tnmk.robocode.common.movement.oscillator.OscillatorContext;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import org.tnmk.robocode.common.radar.botlock.RadarBotLockContext;
import org.tnmk.robocode.common.radar.scanall.ScanAllRobotsRadar;
import org.tnmk.robocode.common.robot.gft.oldalgorithm.GFTAimGun;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class TheUnfoldingRobot extends AdvancedRobot {
    private static int loopIndex = 0;
    private RadarBotLockContext radarBotLockContext = new RadarBotLockContext(this);

    private TheUnfoldingMovement theUnfoldingMovement = new TheUnfoldingMovement(this);

    private OscillatorContext oscillatorContext = new OscillatorContext(this);
    private GFTAimGun gftAimGun = new GFTAimGun(this);
    private DustBunnyAntiGravityMovement dustBunnyAntiGravityMovement = new DustBunnyAntiGravityMovement(this);
    private EnemyDodgeMovement enemyDodgeMovement;
    private ScanAllRobotsRadar scanAllRobotsRadar = new ScanAllRobotsRadar(this);
    public void run() {
        enemyDodgeMovement = new EnemyDodgeMovement(this);
        HiTechDecorator.decorate(this);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        scanAllRobotsRadar.scanAll();
//        while (true) {
//            RadarBotLockHelper.setTurnRadar(radarBotLockContext);
//            execute();
//            loopIndex++;
//        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        theUnfoldingMovement.onScannedRobot(scannedRobotEvent);
//        dustBunnyAntiGravityMovement.onScannedRobot(scannedRobotEvent);
//        RadarBotLockHelper.onScannedRobot(radarBotLockContext, scannedRobotEvent);
//        OscillatorHelper.setMovement(oscillatorContext, scannedRobotEvent, 185, 200);
//        enemyDodgeMovement.onScannedRobot(scannedRobotEvent);
        gftAimGun.onScannedRobot(scannedRobotEvent);
        execute();
    }

    private void log(String message) {
        LogHelper.logAdvanceRobot(this, loopIndex, message);
    }

}
