package org.tnmk.robocode.common.radar.scanall;

import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.helper.Move2DHelper;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;

public class AllEnemiesScanRadar {
    private final AdvancedRobot robot;
    private ScanMode scanMode = ScanMode.INIT_360;
    private final AllEnemiesObservationContext allEnemiesObservationContext;


    public AllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    public void runInit(){
        this.robot.setTurnRadarRight(360);
    }

    public void runLoop(){
        if (scanMode != ScanMode.ALL_ENEMIES && isFinishInitiateScan360()){
            scanMode = ScanMode.ALL_ENEMIES;
            scanAllEnemies();
        }
    }

    public boolean isFinishInitiateScan360() {
        return robot.getTime() >= 360 / RobotPhysics.RADAR_TURN_VELOCITY;
    }

    public void scanAllEnemies() {
        this.robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    /**
     * Collect enemy information.
     *
     * @param scannedRobotEvent
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = new Enemy();
        enemy.setBearing(scannedRobotEvent.getBearing());
        enemy.setDistance(scannedRobotEvent.getDistance());
        enemy.setEnergy(scannedRobotEvent.getEnergy());
        enemy.setHeading(scannedRobotEvent.getHeading());
        enemy.setName(scannedRobotEvent.getName());
        enemy.setSentryRobot(scannedRobotEvent.isSentryRobot());
        enemy.setVelocity(scannedRobotEvent.getVelocity());
        Point2D targetPosition = Move2DHelper.reckonTargetPosition(robot, scannedRobotEvent);
        enemy.setPosition(targetPosition);

        allEnemiesObservationContext.addEnemy(enemy);
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent){
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
    }

}
