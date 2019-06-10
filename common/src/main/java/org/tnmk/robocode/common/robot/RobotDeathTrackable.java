package org.tnmk.robocode.common.robot;

import robocode.RobotDeathEvent;

public interface RobotDeathTrackable {
    void onRobotDeath(RobotDeathEvent robotDeathEvent);
}
