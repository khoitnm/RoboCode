package org.tnmk.robocode.common.movement;

public interface ResetableMoveController extends MoveController {
    /**
     * Reset the MoveController, but the robot may still continue moving.
     */
    void reset();
}
