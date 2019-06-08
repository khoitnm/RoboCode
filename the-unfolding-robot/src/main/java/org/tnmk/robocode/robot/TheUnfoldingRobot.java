package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.backandforth.BackAndForthContext;
import org.tnmk.robocode.common.movement.backandforth.BackAndForthHelper;
import org.tnmk.robocode.common.movement.oscillator.OscillatorContext;
import org.tnmk.robocode.common.movement.oscillator.OscillatorHelper;
import org.tnmk.robocode.common.radar.botlock.RadarBotLockContext;
import org.tnmk.robocode.common.radar.botlock.RadarBotLockHelper;
import org.tnmk.robocode.common.robot.gft.GFTAimGun;
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
    private OscillatorContext oscillatorContext = new OscillatorContext(this);
    private GFTAimGun gftAimGun = new GFTAimGun(this);

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            RadarBotLockHelper.setTurnRadar(radarBotLockContext);
            execute();
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        RadarBotLockHelper.onScannedRobot(radarBotLockContext, scannedRobotEvent);
        OscillatorHelper.setMovement(oscillatorContext, scannedRobotEvent, 185, 200);
        gftAimGun.onScannedRobot(scannedRobotEvent);
        execute();
    }

    private void log(String message) {
        LogHelper.logAdvanceRobot(this, loopIndex, message);
    }

}
