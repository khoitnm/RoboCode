package org.tnmk.robocode.robot;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.radar.RadarFactorLockHelper;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class TheUnfoldingRobot extends AdvancedRobot {
    private static int loopIndex = 0;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            if (DoubleUtils.isConsideredZero(this.getVelocity())) {
                log("Start Advance Ahead 1");
                setAhead(Double.POSITIVE_INFINITY);
                log("Finish Advance Ahead 1");//16 ticks
            }

            if (DoubleUtils.isConsideredZero(getRadarTurnRemaining())) {
                setTurnRadarRight(Double.POSITIVE_INFINITY);
            }

            execute();
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        RadarFactorLockHelper.infiniteLock(this, e);
        fire(1);
        log("Advance fire");
    }

    private void log(String message) {
        LogHelper.logRobot(this, loopIndex, message);
    }

}
