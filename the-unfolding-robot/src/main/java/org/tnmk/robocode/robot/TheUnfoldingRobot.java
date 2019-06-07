package org.tnmk.robocode.robot;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.radar.botlock.RadarBotLockContext;
import org.tnmk.robocode.common.radar.botlock.RadarBotLockHelper;
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

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        int direction = -1;

        while (true) {
            //Run back and forth
            if (DoubleUtils.isConsideredZero(this.getVelocity())) {
                direction = - direction;
                setAhead(80* direction);
            }

            RadarBotLockHelper.doScanner(radarBotLockContext);
            execute();
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        RadarBotLockHelper.onScannedRobot(radarBotLockContext, e);
        setFire(1);
        log("Advance fire");
    }

    private void log(String message) {
        LogHelper.logRobot(this, loopIndex, message);
    }

}
