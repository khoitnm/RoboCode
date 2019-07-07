package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.*;
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

        Set<BotMovement> allPotentialMovments = constructAllPotentialBotMovements(enemy, normAcceleration, avgChangeHeadingRadian);

    }

    public static Set<BotMovement> constructAllPotentialBotMovements(Enemy enemy, int normAcceleration, double changeHeadingRadian) {
        Set<BotMovement> botMovementsWithNormAcceleration = constructAllPotentialPositionsWithSameNormAcceleration(
                enemy.getPosition(), enemy.getVelocity(), normAcceleration, AngleUtils.toRadian(enemy.getHeading()), changeHeadingRadian);
        Set<BotMovement> allPotentialMovments = new HashSet<>(botMovementsWithNormAcceleration);
        if (normAcceleration != 0) {
            int reverseNormAcceleration = Move2DUtils.reverseNormAcceleration(normAcceleration);
            Set<BotMovement> botMovementsWithReversedNormAcceleration = constructAllPotentialPositionsWithSameNormAcceleration(
                    enemy.getPosition(), enemy.getVelocity(), reverseNormAcceleration, AngleUtils.toRadian(enemy.getHeading()), changeHeadingRadian);
            Set<BotMovement> botMovementsWithoutAcceleration = constructAllPotentialPositionsWithSameNormAcceleration(
                    enemy.getPosition(), enemy.getVelocity(), 0, AngleUtils.toRadian(enemy.getHeading()), changeHeadingRadian);
            allPotentialMovments.addAll(botMovementsWithReversedNormAcceleration);
            allPotentialMovments.addAll(botMovementsWithoutAcceleration);
        }
        return allPotentialMovments;
    }

    private static Set<BotMovement> constructAllPotentialPositionsWithSameNormAcceleration(Point2D position, double velocity, int normAcceleration, double headingRadian, double changeHeadingRadian) {
        double turnRateRadians = Rules.getTurnRateRadians(velocity);
        BotMovement mainBotMovement = new BotMovement(position, velocity, normAcceleration, headingRadian, changeHeadingRadian);
        mainBotMovement.setMainMovement(true);
        BotMovement noChangeHeading = (changeHeadingRadian == 0) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, 0);
        BotMovement changeHeadingMaxRight = (changeHeadingRadian == turnRateRadians) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, turnRateRadians);
        BotMovement changeHeadingMaxLeft = (changeHeadingRadian == -turnRateRadians) ? mainBotMovement : new BotMovement(position, velocity, normAcceleration, headingRadian, -turnRateRadians);

        Set<BotMovement> botMovements = new HashSet<>(Arrays.asList(mainBotMovement, noChangeHeading, changeHeadingMaxLeft, changeHeadingMaxRight));
        return botMovements;
    }
}
