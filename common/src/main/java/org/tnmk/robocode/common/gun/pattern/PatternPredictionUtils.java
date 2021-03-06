package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 * <br/>
 * This class try to predict enemy pattern.<br/>
 * View more at {@link EnemyMovePatternIdentifyHelper}
 */
public class PatternPredictionUtils {


    /**
     * @param historyItems      must be not empty
     * @param predictionTime    when is the time that we think the bullet will reach the target.
     * @param enemyMovementArea the area enemy always moving inside. It never move to outside this area (usually the battle field).
     * @return guess new enemy's position and also identify pattern at the predictionTime.
     */
    public static EnemyPrediction predictEnemy(List<Enemy> historyItems, long predictionTime, Rectangle2D enemyMovementArea) {
        Enemy enemy = historyItems.get(0);
        double avgChangeHeadingRadian = EnemyHistoryUtils.averageChangeHeadingRadian(historyItems);
        double avgVelocity = EnemyHistoryUtils.averageVelocity(historyItems);
        return PatternPredictionUtils.predictEnemy(enemy, avgVelocity, avgChangeHeadingRadian, predictionTime, enemyMovementArea);
    }

    /**
     * @param enemy                  latest data in history
     * @param avgChangeHeadingRadian average changing heading of the enemy based recent history items.
     * @param predictionTime         the time that we think the bullet will reach the target.
     * @param avgVelocity            the average velocity of enemy
     * @return guess new enemy's position and moving pattern at the predictionTime based on the latest enemy data and average changing heading.
     */
    public static EnemyPrediction predictEnemy(Enemy enemy, double avgVelocity, double avgChangeHeadingRadian, long predictionTime, Rectangle2D enemyMovementArea) {
        double diff = predictionTime - enemy.getTime();
        double newX, newY;

        EnemyMovePattern enemyMovePattern;
        /**if there is a significant change in heading, use circular path prediction**/
        double enemyHeadingRadian = AngleUtils.toRadian(enemy.getHeading());
        if (Math.abs(avgChangeHeadingRadian) > 0.00001) {
            enemyMovePattern = EnemyMovePattern.CIRCULAR;
            double radius = avgVelocity / avgChangeHeadingRadian;
            double totalChangeHeadingRadian = diff * avgChangeHeadingRadian;
            newY = enemy.getPosition().getY() +
                    Math.sin(enemyHeadingRadian + totalChangeHeadingRadian) * radius -
                    Math.sin(enemyHeadingRadian) * radius
            ;
            newX = enemy.getPosition().getX() + (Math.cos(enemyHeadingRadian) * radius) - (Math.cos(enemyHeadingRadian + totalChangeHeadingRadian) * radius);
        }
        /**if the change in heading is insignificant, use linear path prediction**/
        else {
            if (avgVelocity < 1) {
                enemyMovePattern = EnemyMovePattern.STAY_STILL;
                newY = enemy.getPosition().getY();
                newX = enemy.getPosition().getX();
            } else {
                enemyMovePattern = EnemyMovePattern.LINEAR;
                newY = enemy.getPosition().getY() + Math.cos(enemyHeadingRadian) * enemy.getVelocity() * diff;
                newX = enemy.getPosition().getX() + Math.sin(enemyHeadingRadian) * enemy.getVelocity() * diff;
            }
        }
        Point2D predictionPosition = new Point2D.Double(newX, newY);
        predictionPosition = Move2DUtils.reckonMaximumDestination(enemy.getPosition(), predictionPosition, enemyMovementArea);
        debugPredictionPositionOutsideBattleField(enemy, predictionPosition, enemyMovementArea);
        EnemyPrediction patternPredictionResult = new EnemyPrediction(enemyMovePattern, predictionTime, predictionPosition, avgChangeHeadingRadian, avgVelocity);
        return patternPredictionResult;
    }

    private static void debugPredictionPositionOutsideBattleField(Enemy enemy, Point2D predictionPosition, Rectangle2D enemyMovementArea) {
        if (!GeoMathUtils.checkInsideRectangle(predictionPosition, enemyMovementArea)) {
            String message = String.format("This case should never happens. Predict position's outside battle field: from:" + LogHelper.toString(enemy.getPosition()) + ", to:" + LogHelper.toString(predictionPosition) + ", area:" + LogHelper.toString(enemyMovementArea));
            System.out.println(message);
        }
    }
}
