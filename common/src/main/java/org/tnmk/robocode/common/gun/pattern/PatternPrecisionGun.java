package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.helper.TimeUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.model.enemy.*;
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
        if (!gunStateContext.isAiming()) {
            robot.setTurnGunLeftRadians(aimPrediction.getGunTurnLeftRadian());
            gunStateContext.saveSateAimGun(GunStrategy.PATTERN_PREDICTION, aimPrediction.getBulletPower(), enemyHistory.getName());
        }
//                LogHelper.logSimple(robot, "AimGun(YES): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
//                        "\n\tidentifiedPattern: " + patternIdentification +
//                        "\n\tnewPrediction: " + enemyPrediction +
//                        "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
//                );

        /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/

    }

    private AimPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory) {
        Point2D robotPosition = BattleFieldUtils.constructRobotPosition(robot);
        Rectangle2D enemyMovementBoundary = BattleFieldUtils.constructBattleField(robot);
        EnemyPotentialPositions enemyPotentialPositions = findTimePeriodAllPotentialPositionsStillIntersect(enemyHistory, enemyMovementBoundary);
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
        {
            long timeBulletHitEnemy = robot.getTime() + TimeUtils.toTicks(totalTimePeriodForFiring);
            EnemyPrediction enemyPrediction = new EnemyPrediction(EnemyMovePattern.UNIDENTIFIED, timeBulletHitEnemy, bestPotentialPosition, -1, -1);
            return enemyPrediction;
        };
        //PatternPredictionUtils.predictEnemyBasedOnAllEnemyPotentialPositions(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre);
        return PatternPredictionUtils.predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower, patternPredictionFunction);
    }

    private static EnemyPotentialPositions findTimePeriodAllPotentialPositionsStillIntersect(EnemyHistory enemyHistory, Rectangle2D enemyMovementBoundary) {
        //We'll never check with this ticks number.
        //Hence we must ensure that with this ticks, there's no chance that there will be intersect.
        int maxCheckedTicksNotFound = 20;

        //We'll never check with this ticks number, and always assume that we'll find intersect with this number.
        //Hence we must ensure that with this ticks, there must be intersect.
        int minCheckedTickedFoundResult = 1;
        int checkingTicks = maxCheckedTicksNotFound / 2;
        Rectangle2D foundIntersectArea = null;
        int deltaTicks;
        do {
            Optional<Rectangle2D> intersectArea = findIntersectAreaOfAllPotentialPositions(enemyHistory, checkingTicks, enemyMovementBoundary);
            //1: we'll continue finding with bigger ticks
            //-1: we'll continue finding with smaller ticks.
            int ticksIncrement;
            if (intersectArea.isPresent()) {
                minCheckedTickedFoundResult = checkingTicks;
                foundIntersectArea = intersectArea.get();
                ticksIncrement = 1;
            } else {
                maxCheckedTicksNotFound = checkingTicks;
                ticksIncrement = -1;
            }
            deltaTicks = (maxCheckedTicksNotFound - minCheckedTickedFoundResult) / 2;
            checkingTicks = checkingTicks + ticksIncrement * deltaTicks;
        } while (deltaTicks != 0);

        Point2D targetPosition = GeoMathUtils.calculateCentralPoint(foundIntersectArea);

        EnemyPotentialPositions enemyPotentialPositions = new EnemyPotentialPositions(checkingTicks, targetPosition);
        return enemyPotentialPositions;
    }

    private static Optional<Rectangle2D> findIntersectAreaOfAllPotentialPositions(EnemyHistory enemyHistory, int ticks, Rectangle2D enemyMovementBoundary) {
        List<BotMovementPrediction> potentialPositions = PatternPrecisionUtils.findPotentialPositionsAfterTimePeriod(enemyHistory, ticks, enemyMovementBoundary);
        List<BotBody> potentialBotBodies = potentialPositions.stream().map(PatternPrecisionGun::calculateBotBodyAtPredictionPoint).collect(Collectors.toList());
        Optional<Rectangle2D> intersectArea = BotBodyUtils.reckonIntersectArea(potentialBotBodies);
        return intersectArea;
    }

    private static BotBody calculateBotBodyAtPredictionPoint(BotMovementPrediction botMovementPrediction) {
        return BotBodyFactory.constructBotBody(botMovementPrediction.getPredictPosition());
    }

    /**
     * Fire bullet when finish aiming.
     */
    @Override
    public void runLoop() {
        GunUtils.fireBulletWhenFinishAiming(robot, gunStateContext, HiTechDecorator.BULLET_COLOR);
    }
}
