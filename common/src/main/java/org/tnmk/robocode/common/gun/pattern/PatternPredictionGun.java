package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.*;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 * <pre>
 * One caveat: Cannot deal with multiple robots with the same name.
 * For example, if you add robot "Walls" 5 times into the battle fields, the data will be confused between those 5 robots and not correct anymore.
 * But anyway, that's not my priority at this moment.
 * </pre>
 */
public class PatternPredictionGun implements LoopableRun, OnScannedRobotControl {

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final GunStateContext gunStateContext;

    public PatternPredictionGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, GunStateContext gunStateContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = gunStateContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        String enemyName = scannedRobotEvent.getName();
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
        Enemy enemy = enemyStatisticContext.getEnemyHistory().getLatestHistoryItem();
        double patternCertainty = enemyStatisticContext.getPatternIdentification().getCertainty();
        double bulletPower = BulletPowerHelper.reckonBulletPower(enemy.getDistance(), patternCertainty, robot.getOthers(), robot.getEnergy());
        if (DebugHelper.isDebugGunStrategy()) {
            LogHelper.logSimple(robot, "Aim Pattern: " + enemyStatisticContext.getPatternIdentification() + ", enemy: " + enemyName + ", bulletPower: " + bulletPower + ", distance: " + enemy.getDistance());
        }
        if (bulletPower > 0) {
            aimGun(robot, enemyStatisticContext, bulletPower);
        }
    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext, double bulletPower) {
        if (!gunStateContext.isAiming()) {
            EnemyHistory enemyHistory = enemyStatisticContext.getEnemyHistory();
            PatternIdentification patternIdentification = enemyStatisticContext.getPatternIdentification();

            AimPrediction aimPrediction = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower);
            EnemyPrediction enemyPrediction = aimPrediction.getEnemyPrediction();
            DebugHelper.debug_PatternPredictionGun_predictionPattern(robot, enemyStatisticContext, enemyPrediction);

            /** No matter what is the prediction, always add it into predictionHistory.*/
            EnemyPredictionHistory enemyPredictionHistory = enemyStatisticContext.getEnemyPredictionHistory();
            enemyPredictionHistory.addToHistory(enemyPrediction);

            if (enemyPrediction.getEnemyMovePattern() == patternIdentification.getEnemyMovePattern()) {
//                Point2D enemyPosition = enemyPrediction.getPredictionPosition();
//                Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());

                /**Turn the gun to the correct angle**/
                //double gunBearing = GunUtils.reckonTurnGunLeftNormRadian(robotPosition, enemyPosition, robot.getGunHeadingRadians());
                robot.setTurnGunLeftRadians(aimPrediction.getGunTurnLeftRadian());
                gunStateContext.saveSateAimGun(GunStrategy.PATTERN_PREDICTION, bulletPower, enemyHistory.getName());
                DebugHelper.debug_PatternPredictionGun_TurnGun(robot, enemyStatisticContext, gunStateContext, patternIdentification, enemyPrediction, enemyPredictionHistory);

                /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/
            } else {
                /**
                 * Future prediction is not reliable, so don't aim it.
                 * Gun strategy should not rely on this pattern prediction.
                 */
                DebugHelper.debug_PatternPredictionGun_DontTurnGun(robot, enemyStatisticContext, gunStateContext, patternIdentification, enemyPrediction, enemyPredictionHistory);
            }
        } else {
            /**
             * Don't aim the new target until the old target was done!
             * So don't need to do anything for now.
             */
        }
    }

    private AimPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double bulletPower) {
        PatternPredictionFunction patternPredictionFunction = (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) ->
                PatternPredictionUtils.predictEnemyBasedOnAvgVelocityAndAvgHeadingDelta(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre);
        return PatternPredictionUtils.predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower, patternPredictionFunction);
    }


    /**
     * Fire bullet when finish aiming.
     */
    @Override
    public void runLoop() {
        GunUtils.fireBulletWhenFinishAiming(robot, gunStateContext, HiTechDecorator.BULLET_COLOR);
    }
}
