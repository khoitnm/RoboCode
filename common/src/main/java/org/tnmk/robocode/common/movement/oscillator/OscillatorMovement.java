package org.tnmk.robocode.common.movement.oscillator;

import org.tnmk.robocode.common.movement.MoveStrategyType;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * @see OscillatorHelper
 */
public class OscillatorMovement {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    public OscillatorMovement(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }

    /**
     * @param scannedRobotEvent
     * @param enemyDistance     the distance between this robot and the target
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent, int enemyDistance) {
        if (movementContext.isNone() || movementContext.is(MoveStrategyType.OSCILLATOR)) {
            movementContext.setMoveStrategyType(MoveStrategyType.OSCILLATOR);
            OscillatorHelper.setMovement(movementContext, scannedRobotEvent, Double.POSITIVE_INFINITY, enemyDistance);
        }
    }
}
