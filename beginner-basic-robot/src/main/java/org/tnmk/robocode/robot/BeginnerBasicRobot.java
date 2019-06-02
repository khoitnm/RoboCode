package org.tnmk.robocode.robot;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class BeginnerBasicRobot extends Robot {
    private static int loopIndex = 0;

    public void run() {
        while (true) {
            ahead(500);
            log("Basic ahead 1");
            turnGunRight(360);
            log("Basic turn Gun Right 1");
            back(500);
            log("Basic back");
            turnGunRight(360);
            log("Basic turn Gun Right 2");
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
        log("Basic fight");
    }

    private void log(String message) {
        String finalMessage = String.format("[%s] \t loop[%s] \t %s \t %s", this.getTime(), loopIndex, System.nanoTime(), message);
        out.println(finalMessage);
    }
}