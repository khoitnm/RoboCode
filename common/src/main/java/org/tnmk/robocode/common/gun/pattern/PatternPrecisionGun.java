package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.helper.TimeUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;

public class PatternPrecisionGun implements LoopableRun, OnScannedRobotControl {

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final GunStateContext gunStateContext;

    public PatternPrecisionGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, GunStateContext gunStateContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = gunStateContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        String enemyName = scannedRobotEvent.getName();
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
//        Enemy enemy = enemyStatisticContext.getEnemyHistory().getLatestHistoryItem();
//        double bulletPower = 1;
        aimGun(robot, enemyStatisticContext);

    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext) {
        EnemyHistory enemyHistory = enemyStatisticContext.getEnemyHistory();

        AimPrediction aimPrediction = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory);
        if (aimPrediction == null) {
            DebugHelper.debug_PatternPrecision_NotEnoughTime(robot, enemyHistory);
            return;
        }

//        AimPrediction aimPrediction= PatternPredictionUtils.predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower, (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) -> PatternPredictionUtils.predictEnemyBasedOnAllEnemyPotentialPositions(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre));
        EnemyPrediction enemyPrediction = aimPrediction.getEnemyPrediction();
//            LogHelper.logRobotMovement(robot, "Future prediction: Enemy name: " + enemyStatisticContext.getEnemyName() + ", predictionPattern: " + enemyPrediction.getEnemyMovePattern() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());

        /** No matter what is the prediction, always add it into predictionHistory.*/
        EnemyPredictionHistory enemyPredictionHistory = enemyStatisticContext.getEnemyPredictionHistory();
        enemyPredictionHistory.addToHistory(enemyPrediction);

        /**Turn the gun to the correct angle**/
        robot.setTurnGunLeftRadians(aimPrediction.getGunTurnLeftRadian());
        gunStateContext.saveSateAimGun(GunStrategy.PATTERN_PREDICTION, aimPrediction.getBulletPower(), enemyHistory.getName());
//                LogHelper.logSimple(robot, "AimGun(YES): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
//                        "\n\tidentifiedPattern: " + patternIdentification +
//                        "\n\tnewPrediction: " + enemyPrediction +
//                        "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
//                );

        /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/

    }

    private AimPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory) {
        Point2D robotPosition = BattleFieldUtils.constructRobotPosition(robot);
        EnemyPotentialPositions enemyPotentialPositions = findTimePeriodAllPotentialPositionsStillIntersect(enemyHistory);
        double totalTimePeriodForFiring = enemyPotentialPositions.getTimePeriod();
        Point2D bestPotentialPosition = enemyPotentialPositions.getBestPotentialPosition();
        double distance = robotPosition.distance(bestPotentialPosition);

        double timePeriodToTurnGun = GunUtils.reckonTimePeriodToTurnGun(robot, bestPotentialPosition);
        long ticksForBulletToFly = TimeUtils.toTicks(totalTimePeriodForFiring) - TimeUtils.toTicks(timePeriodToTurnGun);
        if (ticksForBulletToFly < 0) {
            return null;//Not enough time to fire.
        }
        double bulletPower = GunUtils.reckonBulletPower(ticksForBulletToFly, distance);
        if (bulletPower < Rules.MIN_BULLET_POWER) {
            return null;//Not enough time for bullet to reach the enemy.
        }
        PatternPredictionFunction patternPredictionFunction = (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) ->
                PatternPredictionUtils.predictEnemyBasedOnAllEnemyPotentialPositions(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre);
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
