package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.*;
import org.tnmk.common.collection.ListUtils;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;
import robocode.Rules;

public class PatternPrecisionUtils {

    public static List<Point2D> findPotentialPositionsAfterTimePeriod(EnemyHistory enemyHistory, long ticks) {
        //Predict with same accelerator: 3 positions: same heading. Turn left (max), & turn right (max)
        //Predict with reduce accelerator until min velocity, and then increase accelerator until max velocity): 3 positions: same heading. Turn left (max), & turn right (max)
        //Predict with increase accelerator until max velocity: 3 positions: same heading. Turn left (max), & turn right (max)

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
        int reverseNormAcceleration = Move2DUtils.reverseNormAcceleration(normAcceleration);
//        double avgVelocity = EnemyHistoryUtils.averageVelocity(historyItems);
//        return PatternPredictionUtils.predictEnemy(enemy, avgVelocity, avgChangeHeadingRadian, predictionTime, enemyMovementArea);
        Set<BotMovement> botMovementsWithNormAcceleration = constructAllPotentialPositionsAtAllPotentialHeadings(
                enemy.getPosition(), enemy.getVelocity(), normAcceleration, AngleUtils.toRadian(enemy.getHeading()), avgChangeHeadingRadian);
        Set<BotMovement> botMovementsWithReversedNormAcceleration = constructAllPotentialPositionsAtAllPotentialHeadings(
                enemy.getPosition(), enemy.getVelocity(), reverseNormAcceleration, AngleUtils.toRadian(enemy.getHeading()), avgChangeHeadingRadian);
        Set<BotMovement> botMovementsWithoutAcceleration = constructAllPotentialPositionsAtAllPotentialHeadings(
                enemy.getPosition(), enemy.getVelocity(), 0, AngleUtils.toRadian(enemy.getHeading()), avgChangeHeadingRadian);
        Set<BotMovement> allPotentialMovments = new HashSet<>();
        allPotentialMovments.addAll(botMovementsWithNormAcceleration);
        allPotentialMovments.addAll(botMovementsWithReversedNormAcceleration);
        allPotentialMovments.addAll(botMovementsWithoutAcceleration);


    }

    private static Set<BotMovement> constructAllPotentialPositionsAtAllPotentialHeadings(Point2D position, double velocity, int normAcceleration, double headingRadian, double changeHeadingRadian) {
        double turnRateRadians = Rules.getTurnRateRadians(velocity);
        BotMovement mainBotMovement = new BotMovement(position, velocity, normAcceleration, headingRadian, changeHeadingRadian);
        BotMovement noChangeHeading = (changeHeadingRadian == 0) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, 0);
        BotMovement changeHeadingMaxRight = (changeHeadingRadian == turnRateRadians) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, turnRateRadians);
        BotMovement changeHeadingMaxLeft = (changeHeadingRadian == -turnRateRadians) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, -turnRateRadians);

        Set<BotMovement> botMovements = new HashSet<>(Arrays.asList(mainBotMovement, noChangeHeading, changeHeadingMaxLeft, changeHeadingMaxRight));
        return botMovements;
    }
}
