package org.tnmk.robocode.common.movement;

import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;

/**
 * This interface just represents a class which is used for control the movement.
 * <p/>
 * It can control by implementing {@link OnScannedRobotControl}, or {@link LoopableRun}, or {@link InitiableRun}, etc.<br/>
 * It could act as a Movement strategy or a Movement tactic.
 */
public interface MoveController {
}
