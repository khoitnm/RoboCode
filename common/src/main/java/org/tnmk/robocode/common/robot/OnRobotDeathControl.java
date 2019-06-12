package org.tnmk.robocode.common.robot;

import robocode.RobotDeathEvent;

public interface OnRobotDeathControl {
    void onRobotDeath(RobotDeathEvent robotDeathEvent);
}
