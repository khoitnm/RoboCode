package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 */
public class PatternPredictionUtils {


    /**
     * @param historyItems   must be not empty
     * @param predictionTime when is the time that we think the bullet will reach the target.
     * @return guess new enemy's position and also identify pattern at the predictionTime.
     */
    public static EnemyPrediction predictEnemy(List<Enemy> historyItems, long predictionTime) {
        Enemy enemy = historyItems.get(0);
        double avgChangeHeadingRadian = EnemyHistoryUtils.averageChangeHeadingRadian(historyItems);
        return PatternPredictionUtils.predictEnemy(enemy, avgChangeHeadingRadian, predictionTime);
    }

    /**
     * @param enemy                  latest data in history
     * @param avgChangeHeadingRadian average changing heading of the enemy based recent history items.
     * @param predictionTime         the time that we think the bullet will reach the target.
     * @return guess new enemy's position and also identify pattern at the predictionTime.
     *
     * <pre>
     *  TODO improve:
     *      split linear prediction out of circular prediction.
     *      use averageHeading instead of latest heading.
     *      use averageVelocity instead of latest velocity
     * </pre>
     */
    public static EnemyPrediction predictEnemy(Enemy enemy, double avgChangeHeadingRadian, long predictionTime) {
        double diff = predictionTime - enemy.getTime();
        double newX, newY;
        /**if there is a significant change in heading, use circular
         path prediction**/
        double enemyHeadingRadian = AngleUtils.toRadian(enemy.getHeading());
        EnemyMovePattern enemyMovePattern;
        if (Math.abs(avgChangeHeadingRadian) > 0.00001) {
            enemyMovePattern = EnemyMovePattern.CIRCULAR;
            double radius = enemy.getVelocity() / avgChangeHeadingRadian;
            double totalChangeHeadingRadian = diff * avgChangeHeadingRadian;
            newY = enemy.getPosition().getY() +
                    Math.sin(enemyHeadingRadian + totalChangeHeadingRadian) * radius -
                    Math.sin(enemyHeadingRadian) * radius
            ;
            newX = enemy.getPosition().getX() + (Math.cos(enemyHeadingRadian) * radius) - (Math.cos(enemyHeadingRadian + totalChangeHeadingRadian) * radius);
        }
        /**if the change in heading is insignificant, use linear path prediction**/
        else {
            enemyMovePattern = EnemyMovePattern.LINEAR_OR_STAY_STILL;
            newY = enemy.getPosition().getY() + Math.cos(enemyHeadingRadian) * enemy.getVelocity() * diff;
            newX = enemy.getPosition().getX() + Math.sin(enemyHeadingRadian) * enemy.getVelocity() * diff;
        }//TODO stay_still
        Point2D predictionPosition = new Point2D.Double(newX, newY);
        EnemyPrediction patternPredictionResult = new EnemyPrediction(enemyMovePattern, predictionTime, predictionPosition);
        return patternPredictionResult;
    }

}
