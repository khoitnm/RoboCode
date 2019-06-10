package org.tnmk.robocode.common.radar.scanall;

import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.Scannable;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class AllEnemiesScanRadar implements InitiableRun, LoopableRun, Scannable, RobotDeathTrackable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private ScanMode scanMode = ScanMode.INIT_360;
    private int scannedEnemiesEachRound = 0;
    private int radarDirection = 1;

    public AllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void runInit() {
        //It needs to scan 360 degree to count all enemies in the beginning.
        this.robot.setTurnRadarRight(360);
        LogHelper.logAdvanceRobot(this.robot, "radar init scan 360");
    }

    @Override
    public void runLoop() {
        //After finishing runInit (scan 360), the radar can stop at some angle which cannot detect any enemy. But we still need radar continue scanning.
        //That's why we have to trigger scanAllEnemies() here.
        LogHelper.logAdvanceRobot(this.robot, "radar loop scan " + scanMode + ", finish initScan360 " + isFinishInitiateScan360());

        if (isFinishInitiateScan360()) {
            scanMode = ScanMode.ALL_ENEMIES;
            scanAllEnemies();
            LogHelper.logAdvanceRobot(this.robot, "radar started loop scan all enemies");
        }
    }

    /**
     * Collect enemy information.
     *
     * @param scannedRobotEvent
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        LogHelper.logAdvanceRobot(this.robot, "radar onScannedRobot " + scannedRobotEvent.getName());

        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);
        reverseRadarWhenFinishedScanningAllEnemiesInARound();
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
        reverseRadarWhenFinishedScanningAllEnemiesInARound();
    }

    public boolean isFinishInitiateScan360() {
        return robot.getTime() >= 360 / RobotPhysics.RADAR_TURN_VELOCITY;
    }

    private void scanAllEnemies() {
        turnRadarBasedOnDirection(robot, radarDirection);
    }

    private static void turnRadarBasedOnDirection(AdvancedRobot robot, int radarDirection) {
        robot.setTurnRadarRight(radarDirection * Double.POSITIVE_INFINITY);
        robot.scan();//stop the radar and rescan with new direction
        LogHelper.logAdvanceRobot(robot, "radar turn infinity "+ radarDirection);
    }

    private void reverseRadarWhenFinishedScanningAllEnemiesInARound() {
        if (scanMode == ScanMode.ALL_ENEMIES) {
            scannedEnemiesEachRound++;
            LogHelper.logAdvanceRobot(this.robot, "radar scan all enemies mode: " + scannedEnemiesEachRound + ", countEnemies " + robot.getOthers());
            if (scannedEnemiesEachRound >= robot.getOthers()) {
                scannedEnemiesEachRound = 1;//The current robot is already counted as 1, so we should NOT reset this value to 0.
                radarDirection = -radarDirection;
                LogHelper.logAdvanceRobot(this.robot, "radar scan all enemies mode: turn direction " + radarDirection);
                // The turning will be handled in scanAllEnemies(), so we don't need to trigger turnRadar here anymore: turnRadarBasedOnDirection(robot, radarDirection);
            }
        }
    }
}
