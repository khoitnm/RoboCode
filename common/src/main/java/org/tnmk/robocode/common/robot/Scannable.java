package org.tnmk.robocode.common.robot;

import robocode.ScannedRobotEvent;

public interface Scannable {
    void onScannedRobot(ScannedRobotEvent scannedRobotEvent);
}
