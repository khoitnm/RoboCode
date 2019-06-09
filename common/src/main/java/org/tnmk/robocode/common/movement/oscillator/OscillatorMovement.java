package org.tnmk.robocode.common.movement.oscillator;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * @see OscillatorHelper
 */
public class OscillatorMovement  {
    private final AdvancedRobot robot;
    private final OscillatorContext oscillatorContext;

    public OscillatorMovement(AdvancedRobot robot) {
        this.robot = robot;
        this.oscillatorContext = new OscillatorContext(robot);
    }

    /**
     * @param scannedRobotEvent
     * @param enemyDistance the distance between this robot and the target
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent, int enemyDistance) {
        OscillatorHelper.setMovement(oscillatorContext, scannedRobotEvent, Integer.MAX_VALUE, enemyDistance);
    }
}
