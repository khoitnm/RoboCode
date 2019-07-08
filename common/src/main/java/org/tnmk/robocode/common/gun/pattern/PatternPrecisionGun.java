package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.gun.Gun;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.helper.TimeUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPositionPrediction;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPredictionHelper;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.*;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.*;

import java.awt.geom.Point2D;

public class PatternPrecisionGun implements Gun {

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

        AimResult aimResult = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory);
        if (aimResult == null) {
            DebugHelper.debug_GunPatternPrecision_NotEnoughTime(robot, enemyHistory);
            return;
        }

//        AimPrediction aimPrediction= PatternPredictionUtils.predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower, (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) -> PatternPredictionUtils.predictEnemyBasedOnAllEnemyPotentialPositions(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre));
//        EnemyPrediction enemyPrediction = aimPrediction.getEnemyPrediction();
//            LogHelper.logRobotMovement(robot, "Future prediction: Enemy name: " + enemyStatisticContext.getEnemyName() + ", predictionPattern: " + enemyPrediction.getEnemyMovePattern() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());

        /** No matter what is the prediction, always add it into predictionHistory.*/
//        EnemyPredictionHistory enemyPredictionHistory = enemyStatisticContext.getEnemyPredictionHistory();
//        enemyPredictionHistory.addToHistory(enemyPrediction);

        /**Turn the gun to the correct angle**/
        if (!gunStateContext.isAiming()) {
            robot.setTurnGunLeftRadians(aimResult.getGunTurnLeftRadian());
            gunStateContext.saveSateAimGun(GunStrategy.PATTERN_PRECISION, aimResult.getBulletPower(), enemyHistory.getName());
        }
//                LogHelper.logSimple(robot, "AimGun(YES): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
//                        "\n\tidentifiedPattern: " + patternIdentification +
//                        "\n\tnewPrediction: " + enemyPrediction +
//                        "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
//                );

        /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/

    }

    private AimResult predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory) {
        Point2D robotPosition = BattleFieldUtils.constructRobotPosition(robot);
        Rectangle2D enemyMovementBoundary = BattleFieldUtils.constructBattleField(robot);
        EnemyPotentialPositions enemyPotentialPositions = findTimePeriodAllPotentialPositionsStillIntersect(robot, enemyHistory, enemyMovementBoundary);
        double totalTimePeriodForFiring = enemyPotentialPositions.getTimePeriod();
        long totalTicksForFiring = TimeUtils.toTicks(totalTimePeriodForFiring);
        Point2D bestPotentialPosition = enemyPotentialPositions.getBestPotentialPosition();
        double distance = robotPosition.distance(bestPotentialPosition);

        double bulletPower = 0;
        double gunTurnLeftRadian = 0;
        long timeWhenFinishTurningGun;
        double timePeriodToTurnGun = 0;
        long ticksToTurnGun = 0;
        long ticksForBulletToReachEnemy = 0;
        Point2D robotPredictionPosition = robotPosition;
        for (int i = 0; i < 5; i++) {
            gunTurnLeftRadian = GunUtils.reckonTurnGunLeftNormRadian(robotPredictionPosition, bestPotentialPosition, robot.getGunHeadingRadians());
            timePeriodToTurnGun = GunUtils.reckonTimePeriodToTurnGun(gunTurnLeftRadian);
            ticksToTurnGun = TimeUtils.toTicks(timePeriodToTurnGun);
            ticksForBulletToReachEnemy = totalTicksForFiring - ticksToTurnGun;
            if (ticksForBulletToReachEnemy < 0) {
                return null;//Not enough time to fire.
            }
            bulletPower = GunUtils.reckonBulletPower(ticksForBulletToReachEnemy, distance);
            if (bulletPower < Rules.MIN_BULLET_POWER) {
                return null;//Not enough time for bullet to reach the enemy.
            }
            RobotPrediction robotPrediction = RobotPredictionHelper.predictPosition(robot, ticksToTurnGun);
            robotPredictionPosition = robotPrediction.getPosition();
        }

        long timeWhenBulletReachEnemy = robot.getTime() + totalTicksForFiring;
        AimResult aimResult = new AimResult(
                robotPredictionPosition,
                gunTurnLeftRadian, bulletPower,
                robot.getTime(), timeWhenBulletReachEnemy, totalTicksForFiring, ticksToTurnGun, ticksForBulletToReachEnemy);
        DebugHelper.debug_GunPatternPrecision_AimResult(robot, aimResult);
        return aimResult;

//        PatternPredictionFunction patternPredictionFunction = (latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre) ->
//        {
//            long timeBulletHitEnemy = robot.getTime() + TimeUtils.toTicks(totalTimePeriodForFiring);
//            EnemyPositionPrediction enemyPrediction = new EnemyPositionPrediction(timeBulletHitEnemy, bestPotentialPosition);
//            return enemyPrediction;
//        };
//        //PatternPredictionUtils.predictEnemyBasedOnAllEnemyPotentialPositions(latestHistoryItems, timeWhenBulletReachEnemy, enemyMovementBoundaryAre);
//        return PatternPredictionUtils.predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower, patternPredictionFunction);
    }

    private static EnemyPotentialPositions findTimePeriodAllPotentialPositionsStillIntersect(AdvancedRobot robot, EnemyHistory enemyHistory, Rectangle2D enemyMovementBoundary) {
        //We'll never check with this ticks number.
        //Hence we must ensure that with this ticks, there's no chance that there will be intersect.
        int maxCheckedTicksNotFound = 20;

        //We'll never check with this ticks number, and always assume that we'll find intersect with this number.
        //Hence we must ensure that with this ticks, there must be intersect.
        int minCheckedTickedFoundResult = 1;
        int checkingTicks = maxCheckedTicksNotFound / 2;
        Rectangle2D foundIntersectArea = null;
        PotentialPositionsWithIntersectArea foundPotentialPositionsWithIntersectArea = null;
        int deltaTicks;
        do {
            PotentialPositionsWithIntersectArea potentialPositionsWithIntersectArea = findIntersectAreaOfAllPotentialPositions(enemyHistory, checkingTicks, enemyMovementBoundary);
            Optional<Rectangle2D> intersectArea = potentialPositionsWithIntersectArea.getIntersectArea();
            //1: we'll continue finding with bigger ticks
            //-1: we'll continue finding with smaller ticks.
            int ticksIncrement;
            if (intersectArea.isPresent()) {
                minCheckedTickedFoundResult = checkingTicks;
                foundIntersectArea = intersectArea.get();
                foundPotentialPositionsWithIntersectArea = potentialPositionsWithIntersectArea;
                ticksIncrement = 1;
            } else {
                maxCheckedTicksNotFound = checkingTicks;
                ticksIncrement = -1;
            }
            deltaTicks = (maxCheckedTicksNotFound - minCheckedTickedFoundResult) / 2;
            checkingTicks = checkingTicks + ticksIncrement * deltaTicks;
        } while (deltaTicks != 0);

        Point2D targetPosition = GeoMathUtils.calculateCentralPoint(foundIntersectArea);
        DebugHelper.debug_GunPatternPrecision_PotentialPositions_and_TargetPosition(robot, foundPotentialPositionsWithIntersectArea, targetPosition);
        EnemyPotentialPositions enemyPotentialPositions = new EnemyPotentialPositions(checkingTicks, targetPosition);
        return enemyPotentialPositions;
    }

    private static PotentialPositionsWithIntersectArea findIntersectAreaOfAllPotentialPositions(EnemyHistory enemyHistory, int ticks, Rectangle2D enemyMovementBoundary) {
        List<BotMovementPrediction> potentialPositions = PatternPrecisionUtils.findPotentialPositionsAfterTimePeriod(enemyHistory, ticks, enemyMovementBoundary);
        List<BotBody> potentialBotBodies = potentialPositions.stream().map(PatternPrecisionGun::calculateBotBodyAtPredictionPoint).collect(Collectors.toList());
        Optional<Rectangle2D> intersectArea = BotBodyUtils.reckonIntersectArea(potentialBotBodies);
        return new PotentialPositionsWithIntersectArea(intersectArea, potentialBotBodies);
//        return intersectArea;
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

    @Override
    public void runInit() {

    }

    @Override
    public void onBulletHit(BulletHitEvent event) {

    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {

    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {

    }

    @Override
    public void onWin(WinEvent winEvent) {

    }
}
