package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.gft.oldalgorithm.GFTAimGun;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class TheUnfoldingGun {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private GFTAimGun gftAimGun;


    public TheUnfoldingGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gftAimGun = new GFTAimGun(robot);
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        gftAimGun.onScannedRobot(scannedRobotEvent);
    }
}
