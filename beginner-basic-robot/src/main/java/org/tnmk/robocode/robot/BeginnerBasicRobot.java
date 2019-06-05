package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.log.LogHelper;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class BeginnerBasicRobot extends Robot {
    private static int loopIndex = 0;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            log("Start Basic ahead 1");
            ahead(80);
            log("Finish Basic ahead 1");

            log("Start Basic Turn Radar Right 1");
            turnRadarRight(360);
            log("Finish Basic Turn Radar Right 1");

            log("Start Basic turn Gun Right 1");
            turnGunRight(360);
            log("Finish Basic turn Gun Right 1");
            log("-----------------------------------");

            log("Start Basic back");
            back(80);
            log("Finish Basic back");

            log("Start Basic Turn Radar Right 2");
            turnRadarRight(360);
            log("Finish Basic Turn Radar Right 2");

            log("Start Basic turn Gun Right 2");
            turnGunRight(360);
            log("Finish Basic turn Gun Right 2");
            log("-----------------------------------");
            log("==============================================");
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
        log("Basic fire");
    }

    private void log(String message) {
        LogHelper.logRobot(this, loopIndex, message);

    }
}