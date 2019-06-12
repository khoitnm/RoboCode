package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.backandforth.BackAndForthHelper;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class BackAndForthRobot extends AdvancedRobot {
    private static int loopIndex = 0;
    private final BackAndForthHelper backAndForthHelper = new BackAndForthHelper();

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            this.setTurnRadarRight(Double.POSITIVE_INFINITY);
            BackAndForthHelper.setMovement(this, 200);
            execute();
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
        log("Advance fire");
    }

    private void log(String message) {
        LogHelper.logRobot(this, loopIndex, message);
    }

}
