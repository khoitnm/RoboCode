package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPredictionHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

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
        double bulletPower = 1;
        aimGun(robot, enemyStatisticContext, bulletPower);

    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext, double bulletPower) {
        EnemyHistory enemyHistory = enemyStatisticContext.getEnemyHistory();

        AimPrediction aimPrediction = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower);

//        AimPrediction aimPrediction= PatternPredictionUtils.predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower, (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) -> PatternPredictionUtils.predictEnemyBasedOnAccelerationAndHeadingDelta(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre));
        EnemyPrediction enemyPrediction = aimPrediction.getEnemyPrediction();
//            LogHelper.logRobotMovement(robot, "Future prediction: Enemy name: " + enemyStatisticContext.getEnemyName() + ", predictionPattern: " + enemyPrediction.getEnemyMovePattern() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());

        /** No matter what is the prediction, always add it into predictionHistory.*/
        EnemyPredictionHistory enemyPredictionHistory = enemyStatisticContext.getEnemyPredictionHistory();
        enemyPredictionHistory.addToHistory(enemyPrediction);

        /**Turn the gun to the correct angle**/
        robot.setTurnGunLeftRadians(aimPrediction.getGunTurnLeftRadian());
        gunStateContext.saveSateAimGun(GunStrategy.PATTERN_PREDICTION, bulletPower, enemyHistory.getName());
//                LogHelper.logSimple(robot, "AimGun(YES): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
//                        "\n\tidentifiedPattern: " + patternIdentification +
//                        "\n\tnewPrediction: " + enemyPrediction +
//                        "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
//                );

        /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/

    }

    private AimPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double bulletPower) {
        PatternPredictionFunction patternPredictionFunction = (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) ->
                PatternPredictionUtils.predictEnemyBasedOnAccelerationAndHeadingDelta(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre);
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
