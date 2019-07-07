package org.tnmk.robocode.common.movement;

import org.tnmk.robocode.common.robot.*;

public interface Movement extends InitiableRun, LoopableRun, OnScannedRobotControl, OnHitRobotControl, OnStatusControl, OnCustomEventControl, OnBulletHitControl, OnHitWallControl {
}
