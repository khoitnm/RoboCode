package org.tnmk.robocode.common.movement.oscillator;

import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.movement.MoveStrategy;
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
        if (movementContext.isNone() || movementContext.is(MoveStrategy.OSCILLATOR)) {
            movementContext.setMoveStrategy(MoveStrategy.OSCILLATOR);
            OscillatorHelper.setMovement(movementContext, scannedRobotEvent, Double.POSITIVE_INFINITY, enemyDistance);
        }
    }

    public void onScannedRobot(Enemy enemy, int enemyDistance) {
        if (movementContext.isNone() || movementContext.is(MoveStrategy.OSCILLATOR)) {
            OscillatorHelper.setMovement(movementContext, enemy, Double.POSITIVE_INFINITY, enemyDistance);
        }
    }
}
