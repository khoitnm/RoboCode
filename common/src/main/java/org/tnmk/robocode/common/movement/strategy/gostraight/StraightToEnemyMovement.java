package org.tnmk.robocode.common.movement.strategy.gostraight;

import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.movement.Movement;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import robocode.*;

public class StraightToEnemyMovement implements Movement {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    public StraightToEnemyMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = this.allEnemiesObservationContext.getEnemy(scannedRobotEvent.getName());
        Move2DUtils.setMoveToDestinationWithShortestPath(robot, enemy.getPosition());
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
    public void onStatus(StatusEvent statusEvent) {

    }
}
