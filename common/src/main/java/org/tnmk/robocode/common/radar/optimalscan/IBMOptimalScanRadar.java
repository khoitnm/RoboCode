package org.tnmk.robocode.common.radar.optimalscan;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.tnmk.common.math.MathUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyUtils;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.CustomableEvent;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.*;

import static robocode.util.Utils.normalRelativeAngle;

/**
 * https://www.ibm.com/developerworks/library/j-radar/index.html
 * @deprecated Flaw: still scan redundant areas because the calculation was based on the old enemy's bearing angle (which based on old robot's position, not based on the current robot's position).
 */
@Deprecated
public class IBMOptimalScanRadar implements InitiableRun, Scannable, RobotDeathTrackable, CustomableEvent {
    /**
     * Scan a little bit more degree to make sure that all enemies have not moved outside the radar's scan area since the last time they are scanned.
     */
    private static final double SAFE_EXTRA_SCAN_DEGREE = RobotPhysics.RADAR_TURN_VELOCITY / 2;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private int radarDirection = 1;
    private boolean isScannedAllEnemiesAtLeastOnce = false;

    public IBMOptimalScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void runInit() {
        robot.addCustomEvent(new RadarTurnCompleteCondition(robot));
        robot.setAdjustRadarForGunTurn(true);
        robot.setTurnRadarRight(360);
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        if (customEvent.getCondition() instanceof RadarTurnCompleteCondition) {
            sweep();
        }
    }

    private void sweep() {
        double maxBearingAbs = 0, maxBearing = 0;
        int scannedBots = 0;

        Collection<Enemy> enemies = this.allEnemiesObservationContext.getEnemies();
        for (Enemy enemy : enemies) {
            if (enemy != null && EnemyUtils.isEnemyNew(enemy, robot.getTime())) {
                double bearing = normalRelativeAngle(robot.getHeading() + enemy.getBearing() - robot.getRadarHeading());
                if (Math.abs(bearing) > maxBearingAbs) {
                    maxBearingAbs = Math.abs(bearing);
                    maxBearing = bearing;
                }
                scannedBots++;
            }
        }


        double radarTurn = 180 * radarDirection;
        if (scannedBots == robot.getOthers()) {
            radarTurn = maxBearing + MathUtils.sign(maxBearing) * SAFE_EXTRA_SCAN_DEGREE;
        }

        robot.setTurnRadarRight(radarTurn);
        radarDirection = MathUtils.sign(radarTurn);
        printSweep(robot, radarTurn, enemies);
    }

    private void printSweep(AdvancedRobot robot, double radarTurn, Collection<Enemy> enemies) {
        List<Boolean> isEnemiesUpdated = enemies.stream().map(enemy -> EnemyUtils.isEnemyNew(enemy, robot.getTime())).collect(Collectors.toList());
        LogHelper.logAdvanceRobot(robot, "New sweep " + radarTurn + ", enemies updated: " + isEnemiesUpdated);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);
        allEnemiesObservationContext.addEnemy(enemy);
        setIfEverScannedAllEnemiesAtLeastOnce();
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
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
