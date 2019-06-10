package org.tnmk.robocode.common.radar.scanall;

import org.tnmk.robocode.common.constant.RobotPhysics;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class AllEnemiesScanRadar {
    private final AdvancedRobot robot;
    private ScanMode scanMode = ScanMode.INIT_360;
    private final AllEnemiesObservationContext allEnemiesObservationContext;


    public AllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    public void runInit(){
        //It needs to scan 360 degree to count all enemies in the beginning.
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

    //TODO We need to separate 2 scanning modes because the implementation of this method could be optimized so that it doesn't need to scan 360 degree.
    public void scanAllEnemies() {
        this.robot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    /**
     * Collect enemy information.
     *
     * @param scannedRobotEvent
     */
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent){
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
    }

}
