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
        int direction = -1;

        while (true) {
            //Run back and forth
            if (DoubleUtils.isConsideredZero(this.getVelocity())) {
                direction = - direction;
                setAhead(80* direction);
            }

            if (getRadarTurnRemaining() == 0) {
                setTurnRadarRight(Double.POSITIVE_INFINITY);
            }

            execute();
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        RadarFactorLockHelper.stickyLock(this, e);
        setFire(1);
//        execute();
        log("Advance fire");
    }

    private void log(String message) {
        LogHelper.logRobot(this, loopIndex, message);
    }

}
