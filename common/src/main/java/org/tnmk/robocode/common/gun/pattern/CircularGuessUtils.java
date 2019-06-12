package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 */
public class CircularGuessUtils {
    /**
     * @param historyItems must be not empty
     * @param when         when is the time that we think the bullet will reach the target.
     * @return guess new enemy's position
     */
    public static Point2D.Double guessPosition(List<Enemy> historyItems, long when) {
        Enemy enemy = historyItems.get(0);
        double avgChangeHeading = EnemyHistoryUtils.averageChangeHeadings(historyItems);
        return CircularGuessUtils.guessPosition(enemy, avgChangeHeading, when);
    }

    /**
     * @param enemy            latest data in history
     * @param avgChangeHeading average changing heading of the enemy based recent history items.
     * @param when             the time that we think the bullet will reach the target.
     * @return guess new enemy's position
     *
     * <pre>
     *  TODO improve:
     *      split linear prediction out of circular prediction.
     *      use averageHeading instead of latest heading.
     *      use averageVelocity instead of latest velocity
     * </pre>
     */
    public static Point2D.Double guessPosition(Enemy enemy, double avgChangeHeading, long when) {
        /** time is when our scan data was produced.
         *  diff is then difference between the two **/
        double diff = when - enemy.getTime();
        double newX, newY;
        /**if there is a significant change in heading, use circular
         path prediction**/
        if (Math.abs(avgChangeHeading) > 0.00001) {
            double radius = enemy.getVelocity() / avgChangeHeading;
            double tothead = diff * avgChangeHeading;
            newY = enemy.getPosition().getY() + (Math.sin(enemy.getHeading() + tothead) * radius) - (Math.sin(enemy.getHeading()) * radius);
            newX = enemy.getPosition().getX() + (Math.cos(enemy.getHeading()) * radius) - (Math.cos(enemy.getHeading() + tothead) * radius);
        }
        /**if the change in heading is insignificant, use linear
         path prediction**/
        else {
            newY = enemy.getPosition().getY() + Math.cos(enemy.getHeading()) * enemy.getVelocity() * diff;
            newX = enemy.getPosition().getX() + Math.sin(enemy.getHeading()) * enemy.getVelocity() * diff;
        }
        return new Point2D.Double(newX, newY);
    }


}
