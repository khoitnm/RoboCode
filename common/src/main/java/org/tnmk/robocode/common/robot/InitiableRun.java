package org.tnmk.robocode.common.robot;

import robocode.Robot;

public interface InitiableRun {
    /**
     * This method should be trigger in the beginning of {@link Robot#run()}, but not in the while-loop block.
     */
    void runInit();
}
