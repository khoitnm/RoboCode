package org.tnmk.robocode.common.radar.scanall;

import com.sun.istack.internal.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/**
 * The biggest flaw of this algorithm: sometimes radar couldn't find enough robots.
 * Then it has to scan 360 degree multiple times to find out all enemies before revert scanning.<br/>
 * <p/>
 * The root cause is inside the core of RoboCode's {@link robocode.Robot#onScannedRobot(ScannedRobotEvent)}.<br/>
 */
public class AllEnemiesScanRadar implements LoopableRun, Scannable, RobotDeathTrackable {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private Set<String> scannedEnemiesEachRound = new HashSet<>();
    private boolean isScannedAllEnemiesAtLeastOnce = false;
    private int radarDirection = 1;

    public AllEnemiesScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
    }

    @Override
    public void runLoop() {
        robot.setTurnRadarRight(radarDirection * Double.POSITIVE_INFINITY);
//        robot.scan();
        LogHelper.logAdvanceRobot(robot, "radarDirection " + radarDirection);
    }

    /**
     * Collect enemy information.
     *
     * @param scannedRobotEvent
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        Enemy enemy = EnemyMapper.toEnemy(this.robot, scannedRobotEvent);

//        String message = String.format("Actual enemy at time %s, position {%.2f, %.2f}", robot.getTime(), enemy.getPosition().getX(), enemy.getPosition().getY());
//        LogHelper.logAdvanceRobot(robot, message);

        allEnemiesObservationContext.addEnemy(enemy);
        setIfEverScannedAllEnemiesAtLeastOnce();
        reverseRadarWhenFinishedScanningAllEnemiesInARound(scannedRobotEvent.getName());
    }

    private void setIfEverScannedAllEnemiesAtLeastOnce() {
        if (!isScannedAllEnemiesAtLeastOnce) {
            if (allEnemiesObservationContext.countEnemies() >= this.robot.getOthers()) {//Need ">" comparision in case there are some quick died enemies.
                isScannedAllEnemiesAtLeastOnce = true;
            }
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        allEnemiesObservationContext.removeEnemy(robotDeathEvent.getName());
        reverseRadarWhenFinishedScanningAllEnemiesInARound(null);
    }

    /**
     * @param newScannedEnemyName in case of robotDeathEvent, this newScannedEnemyName will be null.
     */
    private void reverseRadarWhenFinishedScanningAllEnemiesInARound(@Nullable String newScannedEnemyName) {
        if (newScannedEnemyName != null) {
            scannedEnemiesEachRound.add(newScannedEnemyName);
        }
        LogHelper.logAdvanceRobot(this.robot, "scannedEnemiesEachRound: " + scannedEnemiesEachRound + ", countEnemies: " + robot.getOthers());
        if (scannedEnemiesEachRound.size() >= robot.getOthers()) {
            //The current robot is already counted as 1, so we should NOT reset this value to 0.
            removeAllExceptOneElement(scannedEnemiesEachRound, newScannedEnemyName);

            radarDirection = -radarDirection;
            robot.setTurnRadarRight(radarDirection * Double.POSITIVE_INFINITY);
            LogHelper.logAdvanceRobot(this.robot, "changed radar direction " + radarDirection);
            // The turning will be handled in scanAllEnemies(), so we don't need to trigger turnRadar here anymore: turnRadarBasedOnDirection(robot, radarDirection);
        }
    }

    private <T> void removeAllExceptOneElement(Collection<T> collection, @Nullable T exceptElement) {
        collection.clear();
        if (exceptElement != null) {
            collection.add(exceptElement);
        }
    }

    public boolean isScannedAllEnemiesAtLeastOnce() {
        return isScannedAllEnemiesAtLeastOnce;
    }
}
