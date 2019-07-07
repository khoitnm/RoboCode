package org.tnmk.robocode.common.movement.strategy.oscillator;

import org.tnmk.robocode.common.movement.MoveController;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.Movement;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.*;

/**
 * @see OscillatorHelper
 */
public class OscillatorMoveController implements MoveController, Movement {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    public OscillatorMoveController(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }

    /**
     * @param scannedRobotEvent
     * @param expectDistanceToEnemy     the distance between this robot and the target
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent, int expectDistanceToEnemy) {
        if (movementContext.isNone() || movementContext.is(MoveStrategy.OSCILLATOR)) {
            movementContext.changeMoveStrategy(MoveStrategy.OSCILLATOR, this);
            OscillatorHelper.setMovement(movementContext, scannedRobotEvent, Double.POSITIVE_INFINITY, expectDistanceToEnemy);
        }
    }

    @Override
    public void runInit() {

    }

    @Override
    public void runLoop() {

    }

    @Override
    public void onBulletHit(BulletHitEvent event) {

    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {

    }

    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {

    }

    @Override
    public void onHitWall(HitWallEvent hitWallEvent) {

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {

    }

    @Override
    public void onStatus(StatusEvent statusEvent) {

    }
}
