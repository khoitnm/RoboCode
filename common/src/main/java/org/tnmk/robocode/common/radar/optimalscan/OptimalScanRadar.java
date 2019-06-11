package org.tnmk.robocode.common.radar.optimalscan;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.CustomableEvent;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.RobotDeathTrackable;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.*;

/**
 * https://www.ibm.com/developerworks/library/j-radar/index.html
 */
public class OptimalScanRadar implements InitiableRun, Scannable, RobotDeathTrackable, CustomableEvent {
    /**
     * Scan a little bit more degree to make sure that all enemies have not moved outside the radar's scan area since the last time they are scanned.
     */
    private static final double SAFE_EXTRA_SCAN_DEGREE = RobotPhysics.RADAR_TURN_VELOCITY;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private int radarDirection = 1;
    private boolean isScannedAllEnemiesAtLeastOnce = false;

    public OptimalScanRadar(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
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

    //TODO refactor
    private void sweep() {
        Collection<Enemy> enemies = this.allEnemiesObservationContext.getEnemies();
        double normRadarTurnRight;
        if (enemies.isEmpty()) {
            normRadarTurnRight = 360;
        } else {
            normRadarTurnRight = sweepWhenFoundSomeEnemies(robot, enemies);
        }

        normRadarTurnRight += MathUtils.sign(normRadarTurnRight) * SAFE_EXTRA_SCAN_DEGREE;
        robot.setTurnRadarRight(normRadarTurnRight);
        radarDirection = -radarDirection;
        printSweep(robot, normRadarTurnRight, enemies);
    }

    private double sweepWhenFoundSomeEnemies(AdvancedRobot robot, Collection<Enemy> enemies) {
        double normRadarTurnRight;

        Pair<Double, Double> minMaxPositionNormBearing = reckonMinMaxPositionNormBearing(robot, enemies);
        double minPositionNormBearing = minMaxPositionNormBearing.getKey();
        double maxPositionNormBearing = minMaxPositionNormBearing.getValue();

        //-180 to 180
        double normMaxMin = AngleUtils.normalizeDegree(maxPositionNormBearing - minPositionNormBearing);
        double normRadarHeading = AngleUtils.normalizeDegree(robot.getRadarHeading());
        if (normMaxMin < 0) {//A: shortest path: maxBearing turn right to reach minBearing
            if (normRadarHeading < 0) {
                if (normRadarHeading > minPositionNormBearing) {//Aa
                    normRadarTurnRight = AngleUtils.normalizeDegree(maxPositionNormBearing - normRadarHeading);//target - currentHeading
                } else {//Ab
                    normRadarTurnRight = reckonNormRadarTurnRightToNearestEnemyPosition(normRadarHeading, minPositionNormBearing, maxPositionNormBearing);
                }
            } else {
                if (normRadarHeading < maxPositionNormBearing) {//Ad
                    normRadarTurnRight = AngleUtils.normalizeDegree(minPositionNormBearing - normRadarHeading);//target - currentHeading
                } else {//Ac == Ab
                    normRadarTurnRight = reckonNormRadarTurnRightToNearestEnemyPosition(normRadarHeading, minPositionNormBearing, maxPositionNormBearing);
                }
            }
        } else {//B, C, D: normMaxMin >= 0: shortest path: maxBearing turn left to reach minBearing
            if (normRadarHeading < minPositionNormBearing) {
                normRadarTurnRight = AngleUtils.normalizeDegree(maxPositionNormBearing - normRadarHeading);//target - currentHeading
            } else if (normRadarHeading > maxPositionNormBearing) {
                normRadarTurnRight = AngleUtils.normalizeDegree(minPositionNormBearing - normRadarHeading);//target - currentHeading
            } else {
                normRadarTurnRight = reckonNormRadarTurnRightToNearestEnemyPosition(normRadarHeading, minPositionNormBearing, maxPositionNormBearing);
            }
        }
        return normRadarTurnRight;
    }

    /**
     * @param normRadarHeading       the normalized heading radar of current robot (-180 to 180)
     * @param minPositionNormBearing the min (in enemies list) normalized bearing angle compare to root (-180 to 180). View {@link #reckonMinMaxPositionNormBearing(AdvancedRobot, Collection)}
     * @param maxPositionNormBearing the similar meaning of minPositionNormBearing
     * @return reckon the nearest angel which robot's radar must turn to reach either the enemy with min bearing or max bearing(in term of angle, not distance).
     */
    private double reckonNormRadarTurnRightToNearestEnemyPosition(double normRadarHeading, double minPositionNormBearing, double maxPositionNormBearing) {
        double normRadarToMin = minPositionNormBearing - normRadarHeading;//turn right (>0)
        double normRadarToMax = maxPositionNormBearing - normRadarHeading;//turn left (<0)
        double normRadarTurnRight;
        if (Math.abs(normRadarToMin) < Math.abs(normRadarToMax)) {
            normRadarTurnRight = normRadarToMin;
        } else {
            normRadarTurnRight = normRadarToMax;
        }
        return normRadarTurnRight;
    }

    /**
     * @param robot
     * @param enemies
     * @return normalized bearing angles (-180 to 180) from robot's position to enemies' positions.<br/>
     * <ul>
     * <li>key: minPositionNormBearing</li>
     * <li>value: maxPositionNormBearing</li>
     * </ul>
     * Note: those value are calculated based on the current robot's position. So they can be different from {@link Enemy#getBearing()} which was calculated based on the old robot's position (when enemy was scanned)
     */
    private Pair<Double, Double> reckonMinMaxPositionNormBearing(AdvancedRobot robot, Collection<Enemy> enemies) {
        double minPositionNormBearing = 180;
        double maxPositionNormBearing = -180;
        for (Enemy enemy : enemies) {
            Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
            double positionNormBearing = Point2DUtils.reckonNormalizeAngle(robotPosition, enemy.getPosition());
            if (minPositionNormBearing > positionNormBearing) {
                minPositionNormBearing = positionNormBearing;
            }
            if (maxPositionNormBearing < positionNormBearing) {
                maxPositionNormBearing = positionNormBearing;
            }
        }
        return new Pair<>(minPositionNormBearing, maxPositionNormBearing);
    }

    private void printSweep(AdvancedRobot robot, double radarTurn, Collection<Enemy> enemies) {
        List<Long> enemiesUpdatedTime = enemies.stream().map(enemy -> robot.getTime() - enemy.getTime()).collect(Collectors.toList());
        LogHelper.logAdvanceRobot(robot, "New sweep " + radarTurn + ", enemies updated: " + enemiesUpdatedTime);
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
