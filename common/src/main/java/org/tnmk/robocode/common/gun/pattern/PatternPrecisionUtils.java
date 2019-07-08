package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;
import robocode.Rules;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternPrecisionUtils {

    public static List<BotMovementPrediction> findPotentialPositionsAfterTimePeriod(EnemyHistory enemyHistory, long ticks, Rectangle2D enemyMovementBoundary) {

        Enemy enemy = enemyHistory.getLatestHistoryItem();
        List<Enemy> latestHistory = enemyHistory.getLatestHistoryItems(2);
        double avgChangeHeadingRadian = EnemyHistoryUtils.averageChangeHeadingRadian(latestHistory);
        double latestVelocity = enemy.getVelocity();
        double acceleration = 0;
        if (enemyHistory.countHistoryItems() > 1) {
            Enemy previousHistoryItem = latestHistory.get(1);
            double previousVelocity = previousHistoryItem.getVelocity();
            double timePeriod = enemy.getTime() - previousHistoryItem.getTime();
            acceleration = (latestVelocity - previousVelocity) / timePeriod;
        }
        int normAcceleration = Move2DUtils.normalizeAcceleration(acceleration);

        Set<BotMovement> allPotentialMovements = constructAllPotentialBotMovements(enemy, normAcceleration, avgChangeHeadingRadian, enemyMovementBoundary);
        List<BotMovementPrediction> allPredictions = predictMovements(allPotentialMovements, ticks);
        return allPredictions;
    }

    private static List<BotMovementPrediction> predictMovements(Set<BotMovement> allPotentialMovments, long ticks) {
        return allPotentialMovments.stream().map(botMovement -> predictMovement(botMovement, ticks)).collect(Collectors.toList());
    }

    private static BotMovementPrediction predictMovement(BotMovement botMovement, long ticks) {
        //TODO improve performance (and precision) by using Maths formula
        Point2D predictPosition;
        double distance = Move2DUtils.calculateDistanceWithAccelerationAndLimitVelocity(botMovement.getVelocity(), botMovement.getNormAcceleration(), (int) ticks);
        if (botMovement.getHeadingChangingRateRadians() > 0.0001) {
            //run in circle
            double avgVelocity = distance / (double) ticks;
            double avgRadius = avgVelocity / botMovement.getHeadingChangingRateRadians();
            double totalHeadingChangeRadian = ticks * botMovement.getHeadingChangingRateRadians();
            double currentHeadingRadian = botMovement.getHeadingRadians();
            double newHeadingRadian = currentHeadingRadian + totalHeadingChangeRadian;
            double newX = botMovement.getCurrentPosition().getX() + (Math.cos(currentHeadingRadian) - Math.cos(newHeadingRadian)) * avgRadius;
            double newY = botMovement.getCurrentPosition().getY() + (Math.sin(newHeadingRadian) - Math.sin(currentHeadingRadian)) * avgRadius;
            predictPosition = new Point2D.Double(newX, newY);
        } else {
            //run in linear
            predictPosition = Move2DUtils.reckonDestination(botMovement.getCurrentPosition(), botMovement.getHeadingRadians(), distance);
        }
//
//
//        double velocity = botMovement.getVelocity();
//        Point2D predictPosition = botMovement.getCurrentPosition();
//        for (int i = 0; i < ticks; i++) {
//            predictPosition = PatternPredictionUtils.predictPosition(predictPosition, botMovement.getHeadingRadians(), velocity, botMovement.getHeadingChangingRateRadians(), 1, botMovement.getEnemyMovementBoundary());
//            velocity += (double) botMovement.getNormAcceleration();
//            if (velocity < 0){
//                velocity = 0;
//            }else if (velocity > Rules.MAX_VELOCITY){
//                velocity = Rules.MAX_VELOCITY;
//            }
//        }
        BotMovementPrediction botMovementPrediction = new BotMovementPrediction(botMovement, predictPosition);
        return botMovementPrediction;
    }

    public static Set<BotMovement> constructAllPotentialBotMovements(Enemy enemy, int normAcceleration, double changeHeadingRadian, Rectangle2D enemyMovementBoundary) {
        Set<BotMovement> botMovementsWithNormAcceleration = constructAllPotentialPositionsWithSameNormAcceleration(
                enemy.getPosition(), enemy.getVelocity(), normAcceleration, AngleUtils.toRadian(enemy.getHeading()), changeHeadingRadian, enemyMovementBoundary);
        Set<BotMovement> allPotentialMovments = new HashSet<>(botMovementsWithNormAcceleration);
        //TODO totally reverse movement direction when enemy is staying still.
        if (normAcceleration != 0) {
            int reverseNormAcceleration = Move2DUtils.reverseNormAcceleration(normAcceleration);
            Set<BotMovement> botMovementsWithReversedNormAcceleration = constructAllPotentialPositionsWithSameNormAcceleration(
                    enemy.getPosition(), enemy.getVelocity(), reverseNormAcceleration, AngleUtils.toRadian(enemy.getHeading()), changeHeadingRadian, enemyMovementBoundary);
            Set<BotMovement> botMovementsWithoutAcceleration = constructAllPotentialPositionsWithSameNormAcceleration(
                    enemy.getPosition(), enemy.getVelocity(), 0, AngleUtils.toRadian(enemy.getHeading()), changeHeadingRadian, enemyMovementBoundary);
            allPotentialMovments.addAll(botMovementsWithReversedNormAcceleration);
            allPotentialMovments.addAll(botMovementsWithoutAcceleration);
        }
        return allPotentialMovments;
    }

    private static Set<BotMovement> constructAllPotentialPositionsWithSameNormAcceleration(Point2D position, double velocity, int normAcceleration, double headingRadian, double changeHeadingRadian, Rectangle2D enemyMovementBoundary) {
        double turnRateRadians = Rules.getTurnRateRadians(velocity);
        BotMovement mainBotMovement = new BotMovement(position, velocity, normAcceleration, headingRadian, changeHeadingRadian, enemyMovementBoundary);
        mainBotMovement.setMainMovement(true);
        BotMovement noChangeHeading = (changeHeadingRadian == 0) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, 0, enemyMovementBoundary);
        BotMovement changeHeadingMaxRight = (changeHeadingRadian == turnRateRadians) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, turnRateRadians, enemyMovementBoundary);
        BotMovement changeHeadingMaxLeft = (changeHeadingRadian == -turnRateRadians) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, -turnRateRadians, enemyMovementBoundary);

        Set<BotMovement> botMovements = new HashSet<>(Arrays.asList(mainBotMovement, noChangeHeading, changeHeadingMaxLeft, changeHeadingMaxRight));
        return botMovements;
    }
}
