package org.tnmk.robocode.common.radar.scanall;

import com.sun.istack.internal.Nullable;
import java.util.HashSet;
import java.util.Set;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/**
 * http://robowiki.net/wiki/Melee_Radar
 */
public class AllEnemiesScanRadar implements InitiableRun, LoopableRun, Scannable, RobotDeathTrackable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private ScanMode scanMode = ScanMode.INIT_360;
    private Set<String> scannedEnemiesEachRound = new HashSet<>();
    private int radarDirection = 1;

    public AllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void runInit() {
        //It needs to scan 360 degree to count all enemies in the beginning.
        this.robot.setTurnRadarRight(360);
        LogHelper.logAdvanceRobot(this.robot, "runInit");
    }

    @Override
    public void runLoop() {
        //After finishing runInit (scan 360), the radar can stop at some angle which cannot detect any enemy. But we still need radar continue scanning.
        //That's why we have to trigger scanAllEnemies() here.
        LogHelper.logAdvanceRobot(this.robot, "runLoop: scanMode: " + scanMode + ", isFinishInitiateScan360: " + isFinishInitiateScan360());

        if (isFinishInitiateScan360()) {
            scanMode = ScanMode.ALL_ENEMIES;
            scanAllEnemies();
            LogHelper.logAdvanceRobot(this.robot, "runLoop: finished scanAllEnemies()");
        }
    }

    /**
     * Collect enemy information.
     *
     * @param scannedRobotEvent
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);
        reverseRadarWhenFinishedScanningAllEnemiesInARound(scannedRobotEvent.getName());
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
        reverseRadarWhenFinishedScanningAllEnemiesInARound(null);
    }

    public boolean isFinishInitiateScan360() {
        return robot.getTime() >= 360 / RobotPhysics.RADAR_TURN_VELOCITY;
    }

    private void scanAllEnemies() {
        turnRadarBasedOnDirection(robot, radarDirection);
    }

    private static void turnRadarBasedOnDirection(AdvancedRobot robot, int radarDirection) {
        robot.setTurnRadarRight(radarDirection * Double.POSITIVE_INFINITY);
        LogHelper.logAdvanceRobot(robot, "radarDirection " + radarDirection);
    }

    /**
     * @param newScannedEnemyName in case of robotDeathEvent, this newScannedEnemyName will be null.
     */
    private void reverseRadarWhenFinishedScanningAllEnemiesInARound(@Nullable String newScannedEnemyName) {
        if (scanMode == ScanMode.ALL_ENEMIES) {
            if (newScannedEnemyName != null) {
                scannedEnemiesEachRound.add(newScannedEnemyName);
            }
            LogHelper.logAdvanceRobot(this.robot, "scannedEnemiesEachRound: " + scannedEnemiesEachRound + ", countEnemies: " + robot.getOthers());
            if (scannedEnemiesEachRound.size() >= robot.getOthers()) {
                scannedEnemiesEachRound.clear();
                if (newScannedEnemyName != null) {//The current robot is already counted as 1, so we should NOT reset this value to 0.
                    scannedEnemiesEachRound.add(newScannedEnemyName);
                }
                radarDirection = -radarDirection;
                robot.setTurnRadarRight(radarDirection * Double.POSITIVE_INFINITY);
                LogHelper.logAdvanceRobot(this.robot, "changed radar direction " + radarDirection);
                // The turning will be handled in scanAllEnemies(), so we don't need to trigger turnRadar here anymore: turnRadarBasedOnDirection(robot, radarDirection);
            }
        }
    }
}
