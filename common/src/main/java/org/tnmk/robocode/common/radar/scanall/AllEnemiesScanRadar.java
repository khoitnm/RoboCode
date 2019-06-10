package org.tnmk.robocode.common.radar.scanall;

import org.tnmk.robocode.common.constant.RobotPhysics;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class AllEnemiesScanRadar {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private ScanMode scanMode = ScanMode.INIT_360;
    private int scannedEnemiesEachRound = 0;
    private int turnDirection = 1;

    public AllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    public void runInit() {
        //It needs to scan 360 degree to count all enemies in the beginning.
        this.robot.setTurnRadarRight(360);
    }

    public void runLoop() {
        //After finishing runInit (scan 360), the radar can stop at some angle which cannot detect any enemy. But we still need radar continue scanning.
        //That's why we have to trigger scanAllEnemies() here.
        if (scanMode != ScanMode.ALL_ENEMIES && isFinishInitiateScan360()) {
            scanMode = ScanMode.ALL_ENEMIES;
            scanAllEnemies();
        }
    }

    /**
     * Collect enemy information.
     *
     * @param scannedRobotEvent
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);
        reverseRadarWhenFinishedScanningAllEnemiesInARound();
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
        reverseRadarWhenFinishedScanningAllEnemiesInARound();
    }

    public boolean isFinishInitiateScan360() {
        return robot.getTime() >= 360 / RobotPhysics.RADAR_TURN_VELOCITY;
    }

    private void scanAllEnemies() {
        setTurnRadarBasedOnDirection(robot, turnDirection);
    }

    private void setTurnRadarBasedOnDirection(AdvancedRobot robot, int turnDirection) {
        if (turnDirection > 0) {
            robot.setTurnRightRadians(Double.POSITIVE_INFINITY);
        } else {
            robot.setTurnLeftRadians(Double.POSITIVE_INFINITY);
        }
    }

    private void reverseRadarWhenFinishedScanningAllEnemiesInARound(){
        if (scanMode == ScanMode.ALL_ENEMIES) {
            scannedEnemiesEachRound++;
            if (scannedEnemiesEachRound >= allEnemiesObservationContext.countEnemies()) {
                scannedEnemiesEachRound = 0;
                turnDirection = -turnDirection;
                // The turning will be handled in scanAllEnemies(), so we don't need to trigger turnRadar here anymore: setTurnRadarBasedOnDirection(robot, turnDirection);
            }
        }
    }
}
