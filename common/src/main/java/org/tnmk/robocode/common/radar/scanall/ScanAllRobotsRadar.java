package org.tnmk.robocode.common.radar.scanall;

import robocode.AdvancedRobot;

public class ScanAllRobotsRadar {
    private final AdvancedRobot robot;

    public ScanAllRobotsRadar(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void scanAll(){
        this.robot.turnRadarRightRadians(Double.POSITIVE_INFINITY);
    }
}
