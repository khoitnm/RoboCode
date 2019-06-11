package org.tnmk.robocode.common.radar.scanall;

import java.util.LinkedHashMap;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * Copied at http://robowiki.net/wiki/Melee_Radar.
 * It also got the same flaw of {@link AllEnemiesScanRadar}.
 */
public class WikiAllEnemiesScanRadar implements LoopableRun, Scannable, RobotDeathTrackable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;


    private boolean isScannedAllEnemiesAtLeastOnce = false;
    private LinkedHashMap<String, Double> enemyHashMap = new LinkedHashMap<>(5, 2, true);
    private double scanDir = 1;
    private Object sought;

    public WikiAllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void runLoop() {
        robot.setTurnRadarRightRadians(scanDir * Double.POSITIVE_INFINITY);
        robot.scan();
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        enemyHashMap.remove(robotDeathEvent.getName());
        sought = null;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);
        setIfEverScannedAllEnemiesAtLeastOnce();


        String name = scannedRobotEvent.getName();
        enemyHashMap.put(name, robot.getHeadingRadians() + scannedRobotEvent.getBearingRadians());

        if ((name == sought || sought == null) && enemyHashMap.size() == robot.getOthers()) {
            scanDir = Utils.normalRelativeAngle(enemyHashMap.values().iterator().next() - robot.getRadarHeadingRadians());
            sought = enemyHashMap.keySet().iterator().next();
        }

    }

    private void setIfEverScannedAllEnemiesAtLeastOnce() {
        if (!isScannedAllEnemiesAtLeastOnce) {
            if (allEnemiesObservationContext.countEnemies() >= this.robot.getOthers()) {//Need ">" comparision in case there are some quick died enemies.
                isScannedAllEnemiesAtLeastOnce = true;
            }
        }
    }


    public boolean isScannedAllEnemiesAtLeastOnce() {
        return isScannedAllEnemiesAtLeastOnce;
    }
}
