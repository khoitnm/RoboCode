package org.tnmk.robocode.common.radar.optimalscan;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.math.Point2DUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyMapper;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
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
        double normRadarTurnRight;
        double minPositionNormBearing = 180;
        double maxPositionNormBearing = -180;

        Collection<Enemy> enemies = this.allEnemiesObservationContext.getEnemies();
        if (enemies.isEmpty()) {
            normRadarTurnRight = 360;
        } else {
            for (Enemy enemy : enemies) {
                Point2D robotPosition = new Double(robot.getX(), robot.getY());
                double positionNormBearing = Point2DUtils.reckonNormalizeAngle(robotPosition, enemy.getPosition());
                if (minPositionNormBearing > positionNormBearing) {
                    minPositionNormBearing = positionNormBearing;
                }
                if (maxPositionNormBearing < positionNormBearing) {
                    maxPositionNormBearing = positionNormBearing;
                }
            }

            //-360 to 360
            double normMaxMin = AngleUtils.normalizeDegree(maxPositionNormBearing - minPositionNormBearing);
            double normRadarHeading = AngleUtils.normalizeDegree(robot.getRadarHeading());
            if (normMaxMin < 0) {//A: shortest path: max turn right to reach min
                if (normRadarHeading < 0) {
                    if (normRadarHeading > minPositionNormBearing) {//Aa
                        normRadarTurnRight = AngleUtils.normalizeDegree(maxPositionNormBearing - normRadarHeading);//target - currentHeading
                    } else {//Ab
                        //keep radarTurnLeftDirection the same
                        double normRadarToMin = minPositionNormBearing - normRadarHeading;//turn right (>0)
                        double normRadarToMax = maxPositionNormBearing - normRadarHeading;//turn left (<0)
                        if (Math.abs(normRadarToMin) < Math.abs(normRadarToMax)) {
                            normRadarTurnRight = normRadarToMin;
                        } else {
                            normRadarTurnRight = normRadarToMax;
                        }
                    }
                } else {
                    if (normRadarHeading < maxPositionNormBearing) {//Ad
                        normRadarTurnRight = AngleUtils.normalizeDegree(minPositionNormBearing - normRadarHeading);//target - currentHeading
                    } else {//Ac == Ab
                        //keep radarTurnLeftDirection the same
                        double normRadarToMin = minPositionNormBearing - normRadarHeading;//turn right (>0)
                        double normRadarToMax = maxPositionNormBearing - normRadarHeading;//turn left (<0)
                        if (Math.abs(normRadarToMin) < Math.abs(normRadarToMax)) {
                            normRadarTurnRight = normRadarToMin;
                        } else {
                            normRadarTurnRight = normRadarToMax;
                        }
                    }
                }
            } else {//B, C, D: normMaxMin >= 0: shortest path: max turn left to reach min
                if (normRadarHeading < minPositionNormBearing) {
                    normRadarTurnRight = AngleUtils.normalizeDegree(maxPositionNormBearing - normRadarHeading);//target - currentHeading
                    //keep the same
                } else if (normRadarHeading > maxPositionNormBearing) {
                    normRadarTurnRight = AngleUtils.normalizeDegree(minPositionNormBearing - normRadarHeading);//target - currentHeading
                } else {
                    double normRadarToMin = minPositionNormBearing - normRadarHeading;//turn right (>0)
                    double normRadarToMax = maxPositionNormBearing - normRadarHeading;//turn left (<0)
                    if (Math.abs(normRadarToMin) < Math.abs(normRadarToMax)) {
                        normRadarTurnRight = normRadarToMin;
                    } else {
                        normRadarTurnRight = normRadarToMax;
                    }
                }
            }
        }

        normRadarTurnRight += MathUtils.sign(normRadarTurnRight) * SAFE_EXTRA_SCAN_DEGREE;
        robot.setTurnRadarRight(normRadarTurnRight);
        radarDirection = -radarDirection;
        printSweep(robot, normRadarTurnRight, enemies);
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
