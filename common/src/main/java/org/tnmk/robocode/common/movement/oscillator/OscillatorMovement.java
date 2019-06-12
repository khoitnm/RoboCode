package org.tnmk.robocode.common.movement.oscillator;

import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.SpecialMovementType;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * @see OscillatorHelper
 */
public class OscillatorMovement {
    private final AdvancedRobot robot;
    private final OscillatorContext oscillatorContext;
    private final MovementContext movementContext;


    public OscillatorMovement(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.oscillatorContext = new OscillatorContext(robot);
        this.movementContext = movementContext;
    }

    /**
     * @param scannedRobotEvent
     * @param enemyDistance     the distance between this robot and the target
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent, int enemyDistance) {
        if (movementContext.isNone() || movementContext.is(SpecialMovementType.OSCILLATOR)) {
            OscillatorHelper.setMovement(oscillatorContext, scannedRobotEvent, Double.POSITIVE_INFINITY, enemyDistance);
        }
    }

    public void onScannedRobot(Enemy enemy, int enemyDistance) {
        if (movementContext.isNone() || movementContext.is(SpecialMovementType.OSCILLATOR)) {
            OscillatorHelper.setMovement(oscillatorContext, enemy, Double.POSITIVE_INFINITY, enemyDistance);
        }
    }
}
